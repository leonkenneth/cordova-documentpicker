var DocumentPicker = function() {};

DocumentPicker.prototype.pick = function(success, failure) {
  cordova.exec(success, failure, "DocumentPicker", "pick", []);
};

DocumentPicker.prototype.getMetadata = function(url, success, failure) {
  cordova.exec(success, failure, "DocumentPicker", "getMetadata", [url]);
};

cordova.addConstructor(function() {

  if (!window.Cordova) {
    window.Cordova = cordova;
  };


  if (!window.plugins) window.plugins = {};
  window.plugins.DocumentPicker = new DocumentPicker();
});
