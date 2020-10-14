var ImageBase64 = function() {};

ImageBase64.prototype.base64 = function(options, success, fail) {
  cordova.exec(function(result) {
    success(result);
  }, function() {
    fail();
  }, "ImageBase64", "base64", [options]);
};

var imageBase64 = new ImageBase64();
module.exports = imageBase64;
