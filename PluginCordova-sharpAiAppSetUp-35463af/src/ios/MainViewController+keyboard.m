//
//  MainViewController+keyboard.m
//  hotShare
//
//  Created by aei on 10/12/16.
//
//

#import "MainViewController+keyboard.h"
#import <objc/runtime.h>
@implementation MainViewController (keyboard)
- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    if (![self.webView respondsToSelector:@selector(setKeyboardDisplayRequiresUserAction:)]) {
        
        [self keyboardDisplayDoesNotRequireUserAction];
    }
    
}

- (void) keyboardDisplayDoesNotRequireUserAction {
    SEL sel = sel_getUid("_startAssistingNode:userIsInteracting:blurPreviousNode:userObject:");
    Class WKContentView = NSClassFromString(@"WKContentView");
    Method method = class_getInstanceMethod(WKContentView, sel);
    IMP originalImp = method_getImplementation(method);
    IMP imp = imp_implementationWithBlock(^void(id me, void* arg0, BOOL arg1, BOOL arg2, id arg3) {
        ((void (*)(id, SEL, void*, BOOL, BOOL, id))originalImp)(me, sel, arg0, TRUE, arg2, arg3);
    });
    method_setImplementation(method, imp);
}

@end
