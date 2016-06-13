var cordova = require('cordova');

var FileBrowser = {
    getImageList : function(successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, 'FileBrowser', 'image', []);
    },
    getAudioList : function(successCallback, errorCallback){
        cordova.exec(successCallback, errorCallback, 'FileBrowser', 'audio', []);
    },
    getVideoList : function(successCallback, errorCallback){
        cordova.exec(successCallback, errorCallback, 'FileBrowser', 'video', []);
    },
    getFileList : function(successCallback, errorCallback){
        cordova.exec(successCallback, errorCallback, 'FileBrowser', 'file', []);
    }
};

module.exports = FileBrowser;