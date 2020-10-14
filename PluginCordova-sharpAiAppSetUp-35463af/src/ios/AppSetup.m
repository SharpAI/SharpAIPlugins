#import "AppSetup.h"
#import <WebKit/WebKit.h>
#import "MainViewController.h"
#import "CDVThemeableBrowser.h"
//#import "hotshare-Swift.h"

@interface AppSetup ()
{
    BOOL applicationWillEnterForeground;
}

@end


@implementation AppSetup

-(void)pluginInitialize{
    
    [[NSNotificationCenter defaultCenter]addObserver:self
                                            selector:@selector(appSetupPluginWillEnterForegroundNotification:)
                                                name:UIApplicationWillEnterForegroundNotification
                                              object:nil];
    [[NSNotificationCenter defaultCenter]addObserver:self
                                            selector:@selector(appSetupPluginOnApplicationDidBecomeActive:)
                                                name:UIApplicationDidBecomeActiveNotification
                                              object:nil];
    
    [[NSNotificationCenter defaultCenter]addObserver:self
                                            selector:@selector(appSetupPluginDidEnterBackgroundNotification:)
                                                name:UIApplicationDidEnterBackgroundNotification
                                              object:nil];
}


-(void)appSetupPluginWillEnterForegroundNotification:(NSNotification *)notification {
    NSLog(@"appSetupPluginWillEnterForegroundNotification!");
    applicationWillEnterForeground = true;
}

- (void)appSetupPluginDidEnterBackgroundNotification:(NSNotification *)notification{
    
    NSLog(@"appSetupPluginDidEnterBackgroundNotification!");
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    if ([self.webView isKindOfClass:[WKWebView class]]) {
        WKWebView *webview = (WKWebView *)self.webView;
        NSLog(@"webview url:%@",webview.URL.absoluteString);
        if (webview.URL) {
            NSLog(@"scheme:%@\n host:%@ path:%@ query:%@ ",[webview.URL scheme],[webview.URL host],[webview.URL path],[webview.URL query]);
            NSMutableString *path = [NSMutableString stringWithFormat:@"%@",[webview.URL path]];
            if ([webview.URL query]) {
                [path appendFormat:@"?%@",[webview.URL query]];
            }
            [defaults setObject:path forKey:@"webViewURL_path"];
            [defaults setValue:[webview.URL port] forKey:@"webViewURL_port"];
            [defaults setValue:@"false" forKey:@"webReloaded"];
            [defaults synchronize];
        }
    }
    applicationWillEnterForeground = false;
}


- (void)reloadWebViewHandle:(WKWebView *)webview withString:(NSString *)urlStr{
    if ([webview isLoading]) {
        [webview stopLoading];
    };
    NSURLRequest* request = [NSURLRequest requestWithURL:[NSURL URLWithString:urlStr] cachePolicy:NSURLRequestUseProtocolCachePolicy timeoutInterval:20.0];
    [webview loadRequest:request];
    
//    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
//    
//    [defaults setValue:@"true" forKey:@"webReloaded"];
//    
//    [defaults synchronize];
    
}

- (void)appSetupPluginOnApplicationDidBecomeActive:(NSNotification *)notification {
    
    NSLog(@"appSetupPluginOnApplicationDidBecomeActive!");
    
    BOOL isBlankScreen = YES;
    
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    
    if ([[self getTopPresentedViewController] isKindOfClass:[MainViewController class]]) {
        
        MainViewController *rootViewController = (MainViewController *)[self getTopPresentedViewController];
        
        if (rootViewController.webView) {
            
            if (applicationWillEnterForeground) {
                
                [rootViewController.view bringSubviewToFront:rootViewController.webView];
            }
            if ([rootViewController.webView isKindOfClass:[WKWebView class]]) {
                
                WKWebView *webview = (WKWebView *)rootViewController.webView;
                if (applicationWillEnterForeground) {
                    if (webview.URL) {
                        NSLog(@"WKWebView is load：%@",webview.URL);
                        
                        BOOL location_is_blank = [[webview.URL absoluteString] isEqualToString:@"about:blank"];
                        
                        if (location_is_blank) {
                            return [self.viewController viewDidLoad];
                        }
                        BOOL needReload = false;
                        id localServerPort = [defaults valueForKey:@"localServerPort"];
                        id port = [defaults valueForKey:@"webViewURL_port"];
                        
                        NSString *reloaded = [defaults valueForKey:@"webReloaded"];
                        
                        if ([reloaded isEqualToString:@"true"]) {
                            //已经重新load过
                            return;
                        }
                        if (localServerPort != port) {
                            needReload = true;
                        }
                        
                        NSString *path_str = [defaults objectForKey:@"webViewURL_path"];
                        
                        NSMutableString *pre_path = [NSMutableString stringWithFormat:@"%@",[webview.URL path]];
                        if ([webview.URL query]) {
                            [pre_path appendFormat:@"?%@",[webview.URL query]];
                        }
                        
                        NSString *authTokenKeyValuePair = [defaults valueForKey:@"authTokenKeyValuePair"];
                        NSString *hostUrl =  [NSMutableString stringWithFormat:@"http://localhost:%@",localServerPort];
                        NSString *startPage = [NSMutableString stringWithFormat:@"/?%@",authTokenKeyValuePair];
                        
                        if ([path_str hasPrefix:@"/?cdvToken="]) {
                            
                            if (needReload || ![path_str isEqualToString:startPage]) { //进入后台后存的路径和localserverstart时的不一样
                                
                                hostUrl = [hostUrl stringByAppendingString:startPage];
                                
                                needReload = true;
                                
                                [self reloadWebViewHandle:webview withString:hostUrl];
                                
                                return;
                            }
                        }
                        
                        BOOL location_is_equal = [pre_path isEqualToString:path_str];
                        
                        if (location_is_equal && !needReload) {
                            
                            isBlankScreen = NO;
                            
                            return;
                            
                        }
                         [self reloadWebViewHandle:webview withString:hostUrl];

                        //[self.viewController viewDidLoad];
                        
                    }
                    else{
                        //                        [self showAlertControllerWith:@"load a null url！Reloading" type:@"url"];
                        //                        if ([webview isLoading]) {
                        //                            [webview stopLoading];
                        //                        }
                        //                        NSString *urlStr = [defaults objectForKey:@"webViewURL"];
                        //                        NSURLRequest* request = [NSURLRequest requestWithURL:[NSURL URLWithString:urlStr] cachePolicy:NSURLRequestUseProtocolCachePolicy timeoutInterval:20.0];
                        //                        [webview loadRequest:request];
                        
                        [self.viewController viewDidLoad];
                    }
                }
            }
        }
        else{
            //[self showAlertControllerWith:@"WKWebView has been killed!" type:@"killed"];
        }
    }
    else{
        if ([[self getTopPresentedViewController] isKindOfClass:[CDVThemeableBrowserNavigationController class]]) {
            
            return;
        }
        //[self showAlertControllerWith:@"MainViewController has been covered!" type:@"covered"];
    }
    
}

-(void)showAlertControllerWith:(NSString *)message type:(NSString *)type{
    //提示框
    UIAlertController *alertController = [UIAlertController alertControllerWithTitle:@"提示" message:message preferredStyle:UIAlertControllerStyleAlert];
    
    UIAlertAction *okAction = [UIAlertAction actionWithTitle:@"我知道了" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
        
        if ([type isEqualToString:@"covered"]) {
            
            [[self getTopPresentedViewController] dismissViewControllerAnimated:NO completion:nil];
            
            [[self getTopPresentedViewController] presentViewController:self.viewController animated:NO completion:nil];
        }
        
    }];
    
    [alertController addAction:okAction];
    
    [[self getTopPresentedViewController] presentViewController:alertController animated:YES completion:^{
        
    }];
    
}

-(UIViewController *)getTopPresentedViewController {
    UIViewController *presentingViewController = self.viewController;
    while(presentingViewController.presentedViewController != nil)
    {
        presentingViewController = presentingViewController.
        presentedViewController;
    }
    return presentingViewController;
}

-(void)getVersion:(CDVInvokedUrlCommand*)command{
    
    NSString *version = [[[NSBundle mainBundle] infoDictionary] objectForKey:@"CFBundleShortVersionString"];
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:version];
    
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}
- (void) openSettings:(CDVInvokedUrlCommand*)command{
    NSURL * url = [NSURL URLWithString:UIApplicationOpenSettingsURLString];
    if([[UIApplication sharedApplication] canOpenURL:url]) {
        NSURL *url =[NSURL URLWithString:UIApplicationOpenSettingsURLString];
        [[UIApplication sharedApplication] openURL:url];
    }
}
@end