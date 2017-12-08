/**
 * commons bootstrap plugin
 * requirejs: jquery.min.js, bootstrap.min.js, angular.min.js, bootbox.min.js
 */
angular.module('commons.bootstrap.plugin', []).factory('$commonsPlugin', function($http, $timeout, $locale) {
	return {
		// start using jQuery
		isWindow: $.isWindow,
		type: $.type,
		isPlainObject: $.isPlainObject,
		extend: $.extend,
		trim: $.trim,
        // end using jQuery
		progressbar: function() {
    		var doc = $(document), win = $(window), body = $('body'), bar = body.data('progressbar'), cover = body.data('progresscover'), loading = body.data('progressloading');
    		if (!bar) {
    		    // https://github.com/tobiasahlin/SpinKit
    			bar = $('<div class="bc-progress-cover"></div>'
    			        + '<div class="bc-progress-loading">'
                            + '<div class="spinner">'
                                + '<div class="bounce1"></div>'
                                + '<div class="bounce2"></div>'
                                + '<div class="bounce3"></div>'
                            + '</div>'
                        + '</div>').appendTo('body');
                cover = body.find('div.bc-progress-cover');
                loading = body.find('div.bc-progress-loading');
    			body.data('progressbar', bar);
    			body.data('progresscover', cover);
    			body.data('progressloading', loading);
    		}
    		return {
    			show: function() {
    				var queue = bar.data('queue') || 0;
    				if (queue <= 0) {
    				    cover.height(doc.height());
    				    loading.css({ top: win.height() / 2 + win.scrollTop(), left: win.width() / 2 - loading.width() / 2 });
    				    bar.stop(true).fadeIn();
    				}
    				bar.data('queue', ++queue);
    				return this;
    			},
    			hide: function() {
    				var queue = bar.data('queue') || 0;
    				queue <= 1 && bar.stop(true).fadeOut();
    				bar.data('queue', --queue);
    				return this;
    			}
    		};
    	},
    	alert: function(text) {
    		bootbox.alert(text || '');
    	},
    	confirm: function(text, callback) {
    	    bootbox.confirm(text, callback);
    	}
    };
});
