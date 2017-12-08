/**
 * commons ionic plugin
 * requirejs: ionic.bundle.min.js
 */
angular.module('commons.ionic.plugin', []).factory('$commonsPlugin', function($http, $timeout, $locale, $ionicLoading, $ionicPopup) {
	var $commonsPlugin, class2type = {}, toString = class2type.toString, hasOwn = class2type.hasOwnProperty, rtrim = /^[\s\uFEFF\xA0]+|[\s\uFEFF\xA0]+$/g;
	angular.forEach("Boolean Number String Function Array Date RegExp Object Error".split(" "), function(name) {
		class2type[ "[object " + name + "]" ] = name.toLowerCase();
	});
	return $commonsPlugin = {
		// start copy from jQuery
		isWindow: function( obj ) {
			return obj != null && obj === obj.window;
		},
    	type: function( obj ) {
    		if ( obj == null ) {
    			return obj + "";
    		}
    		// Support: Android < 4.0, iOS < 6 (functionish RegExp)
    		return typeof obj === "object" || typeof obj === "function" ?
    			class2type[ toString.call(obj) ] || "object" :
    			typeof obj;
    	},
    	isPlainObject: function( obj ) {
    		// Not plain objects:
    		// - Any object or value whose internal [[Class]] property is not "[object Object]"
    		// - DOM nodes
    		// - window
    		if ( $commonsPlugin.type( obj ) !== "object" || obj.nodeType || $commonsPlugin.isWindow( obj ) ) {
    			return false;
    		}
    		if ( obj.constructor &&
    				!hasOwn.call( obj.constructor.prototype, "isPrototypeOf" ) ) {
    			return false;
    		}
    		// If the function hasn't returned already, we're confident that
    		// |obj| is a plain object, created by {} or constructed with new Object
    		return true;
    	},
    	extend : function() {
    		var options, name, src, copy, copyIsArray, clone,
    		target = arguments[0] || {},
    		i = 1,
    		length = arguments.length,
    		deep = false;
	    	// Handle a deep copy situation
	    	if ( typeof target === "boolean" ) {
	    		deep = target;
	    		// skip the boolean and the target
	    		target = arguments[ i ] || {};
	    		i++;
	    	}
	    	// Handle case when target is a string or something (possible in deep copy)
	    	if ( typeof target !== "object" && !angular.isFunction(target) ) {
	    		target = {};
	    	}
	    	// extend jQuery itself if only one argument is passed
	    	if ( i === length ) {
	    		target = this;
	    		i--;
	    	}
	    	for ( ; i < length; i++ ) {
	    		// Only deal with non-null/undefined values
	    		if ( (options = arguments[ i ]) != null ) {
	    			// Extend the base object
	    			for ( name in options ) {
	    				src = target[ name ];
	    				copy = options[ name ];
	    				// Prevent never-ending loop
	    				if ( target === copy ) {
	    					continue;
	    				}
	    				// Recurse if we're merging plain objects or arrays
	    				if ( deep && copy && ( $commonsPlugin.isPlainObject(copy) || (copyIsArray = angular.isArray(copy)) ) ) {
	    					if ( copyIsArray ) {
	    						copyIsArray = false;
	    						clone = src && angular.isArray(src) ? src : [];
	    					} else {
	    						clone = src && $commonsPlugin.isPlainObject(src) ? src : {};
	    					}
	    					// Never move original objects, clone them
	    					target[ name ] = $commonsPlugin.extend( deep, clone, copy );
	
	    				// Don't bring in undefined values
	    				} else if ( copy !== undefined ) {
	    					target[ name ] = copy;
	    				}
	    			}
	    		}
	    	}
	    	// Return the modified object
	    	return target;
    	},
    	// Support: Android<4.1, IE<9
        trim: function( text ) {
            return text == null ? "" : ( text + "" ).replace( rtrim, "" );
        },
    	// end copy from jQuery
    	progressbar : function() {
    		return {
    			show : function() {
    				$ionicLoading.show({ template : 'Loading...' });
    				return this;
    			},
    			hide : function() {
    				$ionicLoading.hide();
    				return this;
    			}
    		};
    	},
    	alert : function(text) {
    		$ionicPopup.alert({ template : text || '' });
    	}
    };
});
