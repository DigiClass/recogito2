require.config({
  baseUrl: "/assets/javascripts/",
  fileExclusionRegExp: /^lib$/,
  paths: { marked: '/webjars/marked/0.3.6/marked.min' }
});

require([
  'common/config',
  'document/annotation/common/baseTextApp',
  'document/annotation/tei/selection/highlighter',
  'document/annotation/tei/selection/selectionHandler'
], function(Config, BaseTextApp, Highlighter, SelectionHandler) {

  jQuery(document).ready(function() {
    var teiURL = jsRoutes.controllers.document.DocumentController
          .getRaw(Config.documentId, Config.partSequenceNo).absoluteURL(),

        contentNode = document.getElementById('content'),

        highlighter = new Highlighter(contentNode),

        selector = new SelectionHandler(contentNode, highlighter);

        CETEIcean = new CETEI();

    CETEIcean.getHTML5(teiURL).then(function(data) {
      document.getElementById("content").appendChild(data);
      new BaseTextApp(contentNode, highlighter, selector);
    });
  });

});
