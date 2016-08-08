var DocumentPicker = function() {};

DocumentPicker.prototype.pick = function(success, failure) {
  cordova.exec(success, failure, "DocumentPicker", "pick", []);
};

cordova.addConstructor(function() {

  if (!window.Cordova) {
    window.Cordova = cordova;
  };


  if (!window.plugins) window.plugins = {};
  window.plugins.DocumentPicker = new DocumentPicker();
});
