var cordova = require('cordova/exec');

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
        var win = function () {
            exec(successCallback, errorCallback, 'FileBrowser' ,'browse',[]);
        };
        var fail = function () {
            errorCallback('Aplicativo sem permissões para leitura de arquivo.');
        };

        exec(win, fail, 'FileBrowser', 'getPermissions',[]);
    }
};

module.exports = FileBrowser;