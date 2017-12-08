/**
 * portal services
 */
angular.module('portal.services', []).factory('$portalservices', function($state, $timeout, $commons) {
	return {
	    header: function($scope) {
	        $scope.session = $.context.session;
	        $scope.htimer && $timeout.cancel($scope.htimer);
	        $scope.htimer = $timeout(function() {
	            var path = window.location.href, index = path.indexOf('#');
	            var parent = $scope;
	            while (parent) { (parent.pathname = path.substr(index + 1)) && (parent = parent.$parent) }
	        }, 100);
	        $scope.transitionTo = function(pathname) { $state.transitionTo(pathname) }
	    }
    };
});
