//
//  ActivityViewController.h
//  hotShare
//
//  Created by aei on 2017/3/14.
//
//

#import <UIKit/UIKit.h>

@interface UISocialActivity : NSObject

- (id)activityType;

- (id)activityTitle;

@end

@interface UIActivityViewController (Private)

- (BOOL)_shouldExcludeActivityType:(UIActivity*)activity;

- (id)_availableActivitiesForItems:(id)arg1;

@end

@interface ActivityViewController : UIActivityViewController

@end
