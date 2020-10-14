//
//  CDVViewController+CreateGapView.m
//  hotShare
//
//  Created by aei on 2016/11/25.
//
//

#import "CDVViewController+CreateGapView.h"

@implementation CDVViewController (CreateGapView)
- (void)createGapView
{
    CGRect webViewBounds = self.view.bounds;
    
    webViewBounds.origin = self.view.bounds.origin;
    
    if ([UIDevice currentDevice].systemVersion.floatValue < 9.0f) {
        
        NSString *key = @"CordovaWebViewEngine";
        
        [self.settings removeObjectForKey:[key lowercaseString]];
    }
   
    UIView* view = [self newCordovaViewWithFrame:webViewBounds];
    
    view.autoresizingMask = (UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleHeight);
    [self.view addSubview:view];
    [self.view sendSubviewToBack:view];
}


@end
