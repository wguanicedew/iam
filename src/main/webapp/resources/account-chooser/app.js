/**
 *
 * Our global app functions are in the app namespace
 *
 */
var app = {};
var OIDCclients = [];
var OIDCproviders = [];

/**
 *
 * Begin main
 *
 */

$(function () {

    /**
     * Load Config
     */

    jQuery.ajaxSetup({async:false});

    $.getJSON('resources/account-chooser/conf/clients.json', function (data) {
        OIDCclients = data;
    });

    $.getJSON('resources/account-chooser/conf/providers.json', function (data) {
        OIDCproviders = data;
    });

    jQuery.ajaxSetup({async:true});

    // get some URL parameters and persist them via cookies
    var last_issuer = $.cookie('last_issuer');

    $.each(OIDCproviders, function (key, button) {

    	var redirect_uri = button.client_uri;

        // build a button and append it
    	var btn_class = (button.cssclass) ? "btn-"+button.cssclass : "";

        var $buttonEl = $(" \
        		<div class='span3'> \
        		  <a class='btn " + btn_class + " '> \
        		      <i class='fa fa-"+button.cssclass+"'></i> \
        		       | Sign in with " + button.descriptor + "\
        		  </a> \
        		</div>" );

        $buttonEl.appendTo('#button-container');

        // bind a click event
        $("a", $buttonEl).click(function () {

        	$.cookie('last_issuer', button.issuer);

        	// TODO: make this safer for existing query parameters if they exist by using a parser of some type
        	var redirect_to = redirect_uri + (redirect_uri.indexOf("?") !== -1 ? "&" : "?") + "iss=" + encodeURI(button.issuer);

        	if ($.inArray(redirect_uri, OIDCclients) != -1) {
        		window.location.href = redirect_to;
        	}

        });

    });

});
