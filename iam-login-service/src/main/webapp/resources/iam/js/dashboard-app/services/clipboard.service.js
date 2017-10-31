'use strict'

angular.module('dashboardApp').factory('clipboardService', clipboardService);

clipboardService.$inject = [ '$window' ];

function clipboardService($window) {

  var service = {
    copyToClipboard : copyToClipboard
  };

  var body = angular.element($window.document.body);
  var textarea = angular.element('<textarea/>');
  textarea.css({
    position : 'fixed',
    opacity : '0'
  });

  return service;

  function copyToClipboard(toCopy) {
    textarea.val(toCopy);
    body.append(textarea);
    textarea[0].select();

    try {
      var successful = document.execCommand('copy');
      if (!successful)
        throw successful;
    } catch (err) {
      window.prompt("Copy to clipboard: Ctrl+C, Enter", toCopy);
    }

    textarea.remove();
  }
}