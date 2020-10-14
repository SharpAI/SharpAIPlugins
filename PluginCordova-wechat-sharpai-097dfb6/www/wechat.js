﻿/*
    Cordova WeChat Plugin
    https://github.com/vilic/cordova-plugin-wechat

    by VILIC VANE
    https://github.com/vilic

    MIT License
*/

exports.share = function (message, scene, onfulfilled, onrejected) {
    var ThenFail = window.ThenFail;
    var promise;

    if (ThenFail && !onfulfilled && !onrejected) {
        promise = new ThenFail();
    }

    var text = null;

    if (typeof message == 'string') {
        text = message;
        message = null;
    }

    cordova
        .exec(function () {
            if (promise) {
                promise.resolve();
            } else if (onfulfilled) {
                onfulfilled();
            }
        }, function (err) {
            if (promise) {
                promise.reject(err);
            } else if (onrejected) {
                onrejected(err);
            }
        }, 'WeChat', 'share', [
            {
                message: message,
                text: text,
                scene: scene
            }
        ]);

    return promise;
};

exports.getUserInfo = function (options,onfulfilled, onrejected) {
    var ThenFail = window.ThenFail;
    var promise;

    if (ThenFail && !onfulfilled && !onrejected) {
        promise = new ThenFail();
    }
    cordova
        .exec(function (res) {
            
            if (promise) {
                promise.resolve(res);
            } else if (onfulfilled) {
                onfulfilled(res);
            }
        }, function (err) {
            if (promise) {
                promise.reject(err);
            } else if (onrejected) {
                onrejected(err);
            }
        }, 'WeChat', 'getUserInfo', [options]);

    return promise;
};

exports.isWXAppInstalled = function (onfulfilled, onrejected) {
    var ThenFail = window.ThenFail;
    var promise;

    if (ThenFail && !onfulfilled && !onrejected) {
        promise = new ThenFail();
    }
    cordova
        .exec(function (res) {
            
            if (promise) {
                promise.resolve(res);
            } else if (onfulfilled) {
                onfulfilled(res);
            }
        }, function (err) {
            if (promise) {
                promise.reject(err);
            } else if (onrejected) {
                onrejected(err);
            }
        }, 'WeChat', 'isWXAppInstalled', [{}]);

    return promise;
};

exports.Scene = {
    chosenByUser: 0,
    session: 1,
    timeline: 2
};

exports.ShareType = {
    app: 1,
    emotion: 2,
    file: 3,
    image: 4,
    music: 5,
    video: 6,
    webpage: 7
};