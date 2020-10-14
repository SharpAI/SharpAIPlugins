//
//  CDVWKWebViewEngine+LocalStorage.m
//  hotShare
//
//  Created by aei on 27/10/2016.
//
//

#import "CDVWKWebViewEngine+LocalStorage.h"

@implementation CDVWKWebViewEngine (LocalStorage)

- (void)webView:(WKWebView*)webView didStartProvisionalNavigation:(WKNavigation*)navigation
{
    [self setLocalStorageToWebView:webView];
    [[NSNotificationCenter defaultCenter] postNotification:[NSNotification notificationWithName:CDVPluginResetNotification object:webView]];
}


-(void)setLocalStorageToWebView:(WKWebView *)webView{
    NSError *error = nil;
    NSString *libDir = [NSSearchPathForDirectoriesInDomains(NSLibraryDirectory, NSUserDomainMask, YES) lastObject];
    //NSLog(@"libDir:%@",libDir);
    NSString *webviewlocalStorageDir= [libDir stringByAppendingPathComponent:@"WebKit/LocalStorage"];
    NSString *wkWebviewlocalStorageDir= [libDir stringByAppendingPathComponent:@"WebKit/WebsiteData/LocalStorage"];
    NSFileManager* fm=[NSFileManager defaultManager];
    
    NSMutableDictionary *localStorageDic = [[NSMutableDictionary alloc] init];
    
    if([fm fileExistsAtPath:webviewlocalStorageDir]){
        NSArray *fileList = [[NSArray alloc] init];
        //fileList便是包含有该文件夹下所有文件的文件名及文件夹名的数组
        fileList = [fm contentsOfDirectoryAtPath:webviewlocalStorageDir error:&error];
        for (NSString *file in fileList) {
            
            if ([file hasPrefix:@"http_meteor.local"]) {
                NSString *path = [webviewlocalStorageDir stringByAppendingPathComponent:file];
                NSData *data = [NSData dataWithContentsOfFile:path];
                NSString *fileExtensionName = [file componentsSeparatedByString:@"."].lastObject;
                [localStorageDic setObject:data forKey:fileExtensionName];
            }
        }
    }
    if ([fm fileExistsAtPath:wkWebviewlocalStorageDir]) {
        NSArray *fileList = [[NSArray alloc] init];
        fileList = [fm contentsOfDirectoryAtPath:wkWebviewlocalStorageDir error:&error];
        if (fileList.count) {
            return;
        }
        
        NSNumber *port = webView.URL.port;
        for (NSString *fileExtensionName in localStorageDic.allKeys) {
            //NSLog(@"data:%@",localStorageDic[fileExtensionName]);
            NSString *path = [wkWebviewlocalStorageDir stringByAppendingPathComponent:[NSString stringWithFormat:@"http_localhost_%@.%@",port,fileExtensionName]];
            
            [localStorageDic[fileExtensionName] writeToFile:path options:NSDataWritingAtomic error:&error];
            
        }
    }
}

@end
