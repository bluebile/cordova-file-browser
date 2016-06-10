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
    getFileList : function(successCallback, errorCallback,arrayType){

        var win = function () {
            cordova.exec(successCallback, errorCallback, 'browse',arrayType); 
        };
        var fail = function () {
            errorCallback('Aplicativo sem permissões para leitura de arquivo.');
        };

        cordova.exec(win, fail, 'getPermissions');
    }
};

module.exports = FileBrowser; 