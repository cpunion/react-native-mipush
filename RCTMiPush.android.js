/**
 * Stub of RCTMiPush for Android.
 *
 * @providesModule RCTMiPush
 * @flow
 */
'use strict';

const MiPush = require('react-native').NativeModules.MiPush;

var RCTMiPush = {
  getInitialMessage: function() {
    return MiPush.getInitialMessage();
  },

  setAlias(alias, category) {
    MiPush.setAlias(alias, category);
  },

  unsetAlias(alias, category) {
    MiPush.unsetAlias(alias, category);
  },

  setUserAccount(userAccount, category) {
    MiPush.setUserAccount(userAccount, category);
  },

  unsetUserAccount(userAccount, category) {
    MiPush.unsetUserAccount(userAccount, category);
  },

  subscribe(topic, category) {
    MiPush.subscribe(topic, category);
  },

  unsubscribe(topic, category) {
    MiPush.unsubscribe(topic, category);
  },

  pausePush(category) {
    MiPush.pausePush(category);
  },

  resumePush(category) {
    MiPush.resumePush(category);
  },

  setAcceptTime(startHour, startMin, entHour, endMin, category) {
    MiPush.setAcceptTime(startHour, startMin, entHour, endMin, category);
  },

  getAllAlias() {
    return MiPush.getAllAlias();
  },

  getAllTopics() {
    return MiPush.getAllTopics();
  },

  reportMessageClicked(msgId) {
    MiPush.reportMessageClicked(msgId);
  },

  clearNotification(notifyId) {
    MiPush.clearNotification(notifyId);
  },

  clearAllNotification() {
    MiPush.clearAllNotification();
  },

  setLocalNotificationType(notifyType) {
    MiPush.setLocalNotificationType(notifyType);
  },

  clearLocalNotificationType() {
    MiPush.clearLocalNotificationType();
  },

  getRegId() {
    return MiPush.getRegId();
  }
};

module.exports = RCTMiPush;
