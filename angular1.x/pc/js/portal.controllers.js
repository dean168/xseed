/**
 * portal controllers
 */
angular.module('portal.controllers', []).controller('loginController', function($state, $scope, $commons) {
	$scope.login = function(user) {
		$commons.post({
			url : '/api/account/login',
			data: user,
			success : function(status) {
				if (status.errcode) {
					angular.extend($.context.session, status);
					$.context.rme(GLOBAL_SESSION);
					$state.transitionTo('portal');
				} else {
					$commons.alert(status.message);
				}
			}
		});
	}
}).controller('registerController', function($state, $scope, $commons) {
	$scope.store = function(user) {
		$commons.post({
			url : '/api/account/register',
			data: user,
			success : function(status) {
				if (status.errcode) {
					angular.extend($.context.session, status);
					$.context.rme(GLOBAL_SESSION);
				} else {
					$commons.alert(status.message);
				}
			}
		});
	};
}).controller('portalController', function($state, $scope, $commons, $portalservices) {
    $portalservices.header($scope);
	$state.transitionTo('portal.list');
}).controller('listPortalController', function($state, $scope, $commons, $portalservices) {
}).controller('editPortalController', function($state, $stateParams, $scope, $uibModal, $commons, $portalservices) {
});
