/*
    Cordova Text-to-Speech Plugin
    https://github.com/vilic/cordova-plugin-tts
 
    by VILIC VANE
    https://github.com/vilic
 
    MIT License
*/

#import <Cordova/CDV.h>
#import "CDVTTS.h"

@implementation CDVTTS

- (void)pluginInitialize {
    synthesizer = [AVSpeechSynthesizer new];
    synthesizer.delegate = self;
}

- (void)speechSynthesizer:(AVSpeechSynthesizer*)synthesizer didFinishSpeechUtterance:(AVSpeechUtterance*)utterance {
    CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    if (lastCallbackId) {
        [self.commandDelegate sendPluginResult:result callbackId:lastCallbackId];
        lastCallbackId = nil;
    } else if(callbackId){
        [self.commandDelegate sendPluginResult:result callbackId:callbackId];
        callbackId = nil;
    }
}

- (void)speak:(CDVInvokedUrlCommand*)command {
    if (callbackId) {
        lastCallbackId = callbackId;
    }
    
    callbackId = command.callbackId;
    
    [synthesizer stopSpeakingAtBoundary:AVSpeechBoundaryImmediate];
    
    NSDictionary* options = [command.arguments objectAtIndex:0];
    
    NSString* text = [options objectForKey:@"text"];
    NSString* locale = [options objectForKey:@"locale"];
    double rate = [[options objectForKey:@"rate"] doubleValue];
    
    if (!locale || (id)locale == [NSNull null]) {
        locale = @"en-US";
    }
    
    if (!rate) {
        rate = 1.0;
    }
    
    AVSpeechUtterance* utterance = [[AVSpeechUtterance new] initWithString:text];
    utterance.voice = [AVSpeechSynthesisVoice voiceWithLanguage:locale];
    utterance.rate = (AVSpeechUtteranceMinimumSpeechRate * 1.5 + AVSpeechUtteranceDefaultSpeechRate) / 2.5 * rate * rate;
    utterance.pitchMultiplier = 1.2;
    [synthesizer speakUtterance:utterance];
}

- (void)stop:(CDVInvokedUrlCommand*)command {
    [synthesizer stopSpeakingAtBoundary:AVSpeechBoundaryImmediate];
    CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    if (lastCallbackId) {
        [self.commandDelegate sendPluginResult:result callbackId:lastCallbackId];
        lastCallbackId = nil;
    }
    if(callbackId){
        [self.commandDelegate sendPluginResult:result callbackId:callbackId];
        callbackId = nil;
    }
}
@end
