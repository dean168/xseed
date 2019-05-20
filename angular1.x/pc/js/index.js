var GLOBAL_SESSION = 'portal.session';

(function($) {
	// 实例化 context
	$.context.open(GLOBAL_SESSION);
	// 获取当前 session
	$.ajax({
		type : 'get',
        url : '/api/account/session',
        cache : false,
        dataType : 'json',
		success : function(status) {
			if (status.code === 200) {
				angular.extend($.context.session, status);
			} else {
				window.location.replace('#/login');
			}
		},
		error : function() {
			window.location.replace('#/login');
		}
    });
	// 注册 portal 模块
	angular.module('portal', [ 'ui.router', 'ui.bootstrap', 'commons.bootstrap.plugin', 'basic.commons', 'portal.services', 'portal.controllers' ]).config(function($stateProvider, $urlRouterProvider) {
		$stateProvider.state('login', {
			url : '/login',
			templateUrl : 'views/login.html',
			controller : 'loginController'
		}).state('register', {
			url : '/register',
			templateUrl : 'views/register.html',
			controller : 'registerController'
		}).state('portal', {
			url : '/portal',
			templateUrl : 'views/index.layout.html',
			controller : 'portalController'
		}).state('portal.list', {
			templateUrl : 'views/portal.list.html',
			controller : 'listPortalController'
		}).state('portal.edit', {
			url : '/:id',
			templateUrl : 'views/portal.edit.html',
			controller : 'editPortalController'
		});
		$urlRouterProvider.otherwise('/portal');
	});

})(jQuery);
