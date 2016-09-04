var DocumentPicker = function() {};

DocumentPicker.prototype.pick = function(mimeTypes, success, failure) {
  cordova.exec(success, failure, "DocumentPicker", "pick", [mimeTypes]);
};

DocumentPicker.prototype.get = function(url, success, failure) {
  cordova.exec(success, failure, "DocumentPicker", "get", [url]);
};

cordova.addConstructor(function() {

  if (!window.Cordova) {
    window.Cordova = cordova;
  };


  if (!window.plugins) window.plugins = {};
  window.plugins.DocumentPicker = new DocumentPicker();
});
