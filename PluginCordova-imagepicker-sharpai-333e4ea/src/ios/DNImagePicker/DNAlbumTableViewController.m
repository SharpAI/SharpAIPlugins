//
//  DNAlbumTableViewController.m
//  ImagePicker
//
//  Created by DingXiao on 15/2/10.
//  Copyright (c) 2015年 Dennis. All rights reserved.
//
#import <AssetsLibrary/AssetsLibrary.h>

#import "DNAlbumTableViewController.h"
#import "DNImagePickerController.h"
#import "DNImageFlowViewController.h"
#import "UIViewController+DNImagePicker.h"
#import "DNUnAuthorizedTipsView.h"

static NSString* const dnalbumTableViewCellReuseIdentifier = @"dnalbumTableViewCellReuseIdentifier";

@interface DNAlbumTableViewController ()
@property (nonatomic, strong) ALAssetsLibrary *assetsLibrary;
@property (nonatomic, strong) NSArray *groupTypes;

#pragma mark - dataSources
@property (nonatomic, strong) NSArray *assetsGroups;
@end

@implementation DNAlbumTableViewController

#pragma mark - life cycle
- (void)viewDidLoad {
    [super viewDidLoad];
    [self setupView];
    [self setupData];
    [self loadData];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - mark setup Data and View
- (void)loadData
{
    __typeof__(self) __weak weakSelf = self;
    //__weak typeof(self) weakSelf = self;
    [self loadAssetsGroupsWithTypes:self.groupTypes completion:^(NSArray *groupAssets)
     {
         weakSelf.assetsGroups = groupAssets;
         [weakSelf.tableView reloadData];
         if (weakSelf.assetsGroups.count == 0 ) {
             
             [weakSelf noImage];
         }
     }];
}

- (void)setupData
{
    self.groupTypes = @[@(ALAssetsGroupAll)];
    self.assetsGroups  = [NSMutableArray new];
}

- (void)setupView
{
    self.title = NSLocalizedStringFromTable(@"albumTitle", @"DNImagePicker", @"photos");
    [self createBarButtonItemAtPosition:DNImagePickerNavigationBarPositionRight
                                   text:NSLocalizedStringFromTable(@"cancel", @"DNImagePicker", @"取消")
                                 action:@selector(cancelAction:)];
    
    [self.tableView registerClass:[UITableViewCell class] forCellReuseIdentifier:dnalbumTableViewCellReuseIdentifier];
    UIView *view = [[UIView alloc] initWithFrame:CGRectZero];
    self.tableView.tableFooterView = view;
    
    
}
-(void)noImage{
    UILabel *label = [[UILabel alloc] init];
    label.center = self.view.center;
    label.textAlignment = NSTextAlignmentCenter;
    label.text = @"无照片";
    label.font = [UIFont systemFontOfSize:18];
    label.translatesAutoresizingMaskIntoConstraints = NO;
    //创建x居中的约束
    NSLayoutConstraint * constraintx = [NSLayoutConstraint constraintWithItem:label attribute:NSLayoutAttributeCenterX relatedBy:NSLayoutRelationEqual toItem:self.view attribute:NSLayoutAttributeCenterX multiplier:1 constant:0];
    //创建y居中的约束
    NSLayoutConstraint * constrainty = [NSLayoutConstraint constraintWithItem:label attribute:NSLayoutAttributeCenterY relatedBy:NSLayoutRelationEqual toItem:self.view attribute:NSLayoutAttributeCenterY multiplier:1 constant:-15];
    //创建宽度约束
    NSLayoutConstraint * constraintw = [NSLayoutConstraint constraintWithItem:label attribute:NSLayoutAttributeWidth relatedBy:NSLayoutRelationEqual toItem:self.view attribute:NSLayoutAttributeWidth multiplier:1 constant:0];
    //创建高度约束
    NSLayoutConstraint * constrainth = [NSLayoutConstraint constraintWithItem:label attribute:NSLayoutAttributeHeight relatedBy:NSLayoutRelationEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1 constant:30];
    //添加约束之前，必须将视图加在父视图上
    [self.view addSubview:label];
    [self.view addConstraints:@[constraintx,constrainty,constrainth,constraintw]];
    
}


#pragma mark - ui actions
- (void)cancelAction:(id)sender
{
    DNImagePickerController *navController = [self dnImagePickerController];
    if (navController && [navController.imagePickerDelegate respondsToSelector:@selector(dnImagePickerControllerDidCancel:)]) {
        [navController.imagePickerDelegate dnImagePickerControllerDidCancel:navController];
    }
}

#pragma mark - getter/setter
- (ALAssetsLibrary *)assetsLibrary
{
    if (nil == _assetsLibrary) {
        _assetsLibrary = [[ALAssetsLibrary alloc] init];
    }
    return _assetsLibrary;
}

- (DNImagePickerController *)dnImagePickerController
{
    
    if (nil == self.navigationController
        ||
        ![self.navigationController isKindOfClass:[DNImagePickerController class]])
    {
        NSAssert(false, @"check the navigation controller");
    }
    return (DNImagePickerController *)self.navigationController;
}

- (NSAttributedString *)albumTitle:(ALAssetsGroup *)assetsGroup
{
    NSString *albumTitle = [assetsGroup valueForProperty:ALAssetsGroupPropertyName];
    NSString *numberString = [NSString stringWithFormat:@"  (%@)",@(assetsGroup.numberOfAssets)];
    NSString *cellTitleString = [NSString stringWithFormat:@"%@%@",albumTitle,numberString];
    NSMutableAttributedString *attributedString = [[NSMutableAttributedString alloc] initWithString:cellTitleString];
    [attributedString setAttributes: @{
                                       NSFontAttributeName : [UIFont systemFontOfSize:16.0f],
                                       NSForegroundColorAttributeName : [UIColor blackColor],
                                       }
                              range:NSMakeRange(0, albumTitle.length)];
    [attributedString setAttributes:@{
                                      NSFontAttributeName : [UIFont systemFontOfSize:16.0f],
                                      NSForegroundColorAttributeName : [UIColor grayColor],
                                      } range:NSMakeRange(albumTitle.length, numberString.length)];
    return attributedString;
    
}

- (void)showUnAuthorizedTipsView
{
    DNUnAuthorizedTipsView *view  = [[DNUnAuthorizedTipsView alloc] initWithFrame:self.tableView.frame];
    self.tableView.backgroundView = view;
//    [self.tableView addSubview:view];
}

#pragma mark - Table view data source
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return self.assetsGroups.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:dnalbumTableViewCellReuseIdentifier forIndexPath:indexPath];
    cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
    ALAssetsGroup *group = self.assetsGroups[indexPath.row];
    cell.textLabel.attributedText = [self albumTitle:group];
    
    //choose the latest pic as poster image
    __weak UITableViewCell *blockCell = cell;
    [group enumerateAssetsAtIndexes:[NSIndexSet indexSetWithIndex:group.numberOfAssets-1] options:NSEnumerationConcurrent usingBlock:^(ALAsset *result, NSUInteger index, BOOL *stop) {
        if (result) {
            *stop = YES;
            blockCell.imageView.image = [UIImage imageWithCGImage:result.thumbnail];
        }
    }];
    return cell;
}

- (CGFloat)tableView:(UITableView *)tableView estimatedHeightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 64;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 64;
}

#pragma mark - tableView delegate
- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    ALAssetsGroup *group = self.assetsGroups[indexPath.row];
    NSURL *url = [group valueForProperty:ALAssetsGroupPropertyURL];
    DNImageFlowViewController *imageFlowViewController = [[DNImageFlowViewController alloc] initWithGroupURL:url];
    [self.navigationController pushViewController:imageFlowViewController animated:YES];
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
}


#pragma mark - get assetGroups
- (void)loadAssetsGroupsWithTypes:(NSArray *)types completion:(void (^)(NSArray *assetsGroups))completion
{
    __block NSMutableArray *assetsGroups = [NSMutableArray array];
    __block NSUInteger numberOfFinishedTypes = 0;
    
    for (NSNumber *type in types) {
        __typeof__(self) __weak weakSelf = self;
        //__weak typeof(self) weakSelf = self;
        [self.assetsLibrary enumerateGroupsWithTypes:[type unsignedIntegerValue]
                                          usingBlock:^(ALAssetsGroup *assetsGroup, BOOL *stop)
         {
             if (assetsGroup) {
                 // Filter the assets group
                 [assetsGroup setAssetsFilter:ALAssetsFilterFromDNImagePickerControllerFilterType([[weakSelf dnImagePickerController] filterType])];
                 // Add assets group
                 if (assetsGroup.numberOfAssets > 0) {
                     // Add assets group
                     [assetsGroups addObject:assetsGroup];
                 }
             } else {
                 numberOfFinishedTypes++;
             }
             
             // Check if the loading finished
             if (numberOfFinishedTypes == types.count) {
                 //sort
                 NSArray *sortedAssetsGroups = [assetsGroups sortedArrayUsingComparator:^NSComparisonResult(id obj1, id obj2) {
                     
                     ALAssetsGroup *a = obj1;
                     ALAssetsGroup *b = obj2;
                     
                     NSNumber *apropertyType = [a valueForProperty:ALAssetsGroupPropertyType];
                     NSNumber *bpropertyType = [b valueForProperty:ALAssetsGroupPropertyType];
                     if ([apropertyType compare:bpropertyType] == NSOrderedAscending)
                     {
                         return NSOrderedDescending;
                     }
                     return NSOrderedSame;
                 }];
                 
                 // Call completion block
                 if (completion) {
                     completion(sortedAssetsGroups);
                 }
             }
         } failureBlock:^(NSError *error) {
//             NSLog(@"%@",error.description);
             if ([ALAssetsLibrary authorizationStatus] != ALAuthorizationStatusAuthorized){
                 [self showUnAuthorizedTipsView];
             }
         }];
    }
}
@end
