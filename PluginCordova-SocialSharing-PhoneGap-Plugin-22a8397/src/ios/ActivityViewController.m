//
//  ActivityViewController.m
//  hotShare
//
//  Created by aei on 2017/3/14.
//
//

#import "ActivityViewController.h"

@interface ActivityViewController ()

@end

@implementation ActivityViewController

- (BOOL)_shouldExcludeActivityType:(UIActivity *)activity
{
//    if ([[activity activityType] isEqualToString:@"com.apple.reminders.RemindersEditorExtension"] ||
//        [[activity activityType] isEqualToString:@"com.apple.mobilenotes.SharingExtension"]) {
//        return YES;
//    }
    NSLog(@"activityType:%@\n activityTitle:%@",[activity activityType],[activity activityTitle]);
    BOOL isWeiXin = [[activity activityType] isEqualToString:@"com.tencent.xin.sharetimeline"];
    BOOL isQQ = [[activity activityType] isEqualToString:@"com.tencent.mqq.ShareExtension"];
    BOOL isGuShiTie = [[activity activityType] isEqualToString:@"org.hotshare.everywhere.shareEx"];
    
    if (isWeiXin || isQQ || isGuShiTie) {
        return  YES;
    }
    return [super _shouldExcludeActivityType:activity];
}

- (id)_availableActivitiesForItems:(id)arg1
{
    id activities = [super _availableActivitiesForItems:arg1];
    NSMutableArray *filteredActivities = [NSMutableArray array];
    
    [activities enumerateObjectsUsingBlock:^(UISocialActivity*  _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
        
        NSLog(@"activityType:%@\n activityTitle:%@",[obj activityType],[obj activityTitle]);
        
        BOOL isWeiXin = [[obj activityType] isEqualToString:@"com.tencent.xin.sharetimeline"];
        BOOL isQQ = [[obj activityType] isEqualToString:@"com.tencent.mqq.ShareExtension"];
        BOOL isGuShiTie = [[obj activityType] isEqualToString:@"org.hotshare.everywhere.shareEx"];
        
        if (isWeiXin || isQQ || isGuShiTie ) {
            return ;
        }
        [filteredActivities addObject:obj];
        
    }];
    
    return [NSArray arrayWithArray:filteredActivities];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end
