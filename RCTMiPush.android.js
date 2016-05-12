/**
 * Stub of RCTMiPush for Android.
 *
 * @providesModule RCTMiPush
 * @flow
 */
'use strict';

const MiPush = require('react-native').NativeModules.MiPush;

var RCTMiPush = {
  getInitialMessage: function(callback) {
    callback = callback || function () {};

    MiPush.getInitialMessage(callback);
  }
};

module.exports = RCTMiPush;
