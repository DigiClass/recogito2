/** Contains common high-level functionality needed for text and image 'app' entrypoints **/
define(['common/api', 'common/config'], function(API, Config) {

  var BaseApp = function(highlighter, pageHeader) {
    this.highlighter = highlighter;
    this.header = pageHeader;
  };

  BaseApp.prototype.onAnnotationsLoaded = function(annotations) {
    var urlHash = (window.location.hash) ? window.location.hash.substring(1) : false,
        preselected;

    this.header.incrementAnnotationCount(annotations.length);
    // var startTime = new Date().getTime();
    this.highlighter.initPage(annotations);
    // console.log('took ' + (new Date().getTime() - startTime) + 'ms');

    // TODO needs refactoring to make it work cross-media
    if (urlHash) {
      preselected = highlighter.findById(hash);
      if (preselected)
        editor.open(preselected.annotation, preselected.elements[0].getBoundingClientRect());
    }
  };

  BaseApp.prototype.onAnnotationsLoadError = function(annotations) {
    // TODO visual notification
  };

  BaseApp.prototype.onUpdateAnnotation = function(annotationStub) {
    var self = this;

    self.header.showStatusSaving();
    API.storeAnnotation(annotationStub)
       .done(function(annotation) {
         // Update header info
         self.header.incrementAnnotationCount();
         self.header.updateContributorInfo(Config.me);
         self.header.showStatusSaved();

         // Merge server-provided properties (id, timestamps, etc.) into the annotation
         jQuery.extend(annotationStub, annotation);
         self.highlighter.refreshAnnotation(annotationStub);
       })
       .fail(function(error) {
         self.header.showSaveError(error);
       });
  };

  BaseApp.prototype.onDeleteAnnotation = function(annotation) {
    var self = this;
    API.deleteAnnotation(annotation.annotation_id)
       .done(function() {
         self.header.incrementAnnotationCount(-1);
         self.header.showStatusSaved();
       })
       .fail(function(error) {
         self.header.showSaveError(error);
       });
  };

  return BaseApp;

});
