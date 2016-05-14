/**
 * @providesModule RCTMiPush
 * @flow
 */
'use strict';

var MiPush = require('react-native').NativeModules.MiPush;
/**
 * High-level docs for the RCTMiPush iOS API can be written here.
 */

var RCTMiPush = {
  getInitialMessage: function() {
    return MiPush.getInitialMessage();
  },

  registerMiPush: function () {
    MiPush.registerMiPush();
  },

  registerMiPushAndConnect: function (isConnect, type) {
    MiPush.registerMiPushAndConnect(isConnect, type);
  },

  bindDeviceToken: function(hexDeviceToken) {
    MiPush.bindDeviceToken(hexDeviceToken);
  },

  setAlias: function(alias) {
    MiPush.setAlias(alias);
  },

  unsetAlias: function(alias) {
    MiPush.unsetAlias(alias);
  },

  setAccount: function(account) {
    MiPush.setAccount(account);
  },

  unsetAccount: function(account) {
    MiPush.unsetAccount(account);
  },

  subscribe: function(topic) {
    MiPush.subscribe(topic);
  },

  unsubscribe: function(topic) {
    MiPush.unsubscribe(topic);
  },

  openAppNotify: function(messageId) {
    MiPush.openAppNotify(messageId);
  },

  getAllAliasAsync: function() {
    MiPush.getAllAliasAsync();
  },

  getAllAccountAsync: function() {
    MiPush.getAllAccountAsync();
  },

  getAllTopicAsync: function() {
    MiPush.getAllTopicAsync();
  },

  getSDKVersion: function() {
    return MiPush.getSDKVersion();
  }
};

module.exports = RCTMiPush;
