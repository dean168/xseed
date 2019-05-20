// 错误处理
//window.onerror = function(error, url, line) {
//	alert(error + '\n' + url + ':' + line)
//	return false
//}
// 初始化 $
!window.$ && (window.$ = {});
// context 对象
window.$.context = {
	session: {},
	params: {
		put: function(key, value) {
			!$.context.params.data && ($.context.params.data = {});
			typeof key == 'string' ? ($.context.params.data[key] = value) : angular.extend($.context.params.data, key);
			return this;
		},
		get: function(key) {
			!$.context.params.data && ($.context.params.data = {});
			return $.context.params.data[key];
		},
		take: function(key) {
			var value = $.context.params.get(key);
			delete $.context.params.data[key];
			return value;
		},
		remove: function(key) {
			delete $.context.params.data[key];
		}
	},
	rme: function(name) {
		$.context.session && (window.localStorage[name] = angular.toJson($.context.session));
		return this;
	},
	dme: function(name) {
		delete window.localStorage[name];
		return this;
	},
	open: function(name) {
		var session = window.localStorage[name];
		session && ($.context.session = angular.fromJson(session));
		return this;
	},
	close: function() {
		$.context.session = {};
		$.context.params.data = {};
		return this;
	}
};
/**
 * commons module
 * requirejs: commons.bootstrap.plugin.js || commons.ionic.plugin.js
 */
angular.module('basic.commons', []).directive('bcFocus', function($timeout, $commons) {
	return function(scope, element) {
		return (element.attr('bc-focus') == 'true' || $commons.getValue(scope, element.attr('bc-focus'))) && $timeout(function() {
			element[0].focus();
		}, 300);
	};
}).directive('bcEnter', function($timeout, $commons) {
	return function(scope, element) {
        return element.bind('keypress', function(event) {
            var keyCode = event.which;
            if (keyCode == 13) {
                var func = element.attr('bc-enter');
                func = func.indexOf('(') ? func.substr(0, func.indexOf('(')) : func;
                func = $.trim(func);
                scope[func] && scope[func]();
                return false;
            } else {
                return true;
            }
        });
    };
}).directive('bcIntType', function($commons) { // 只能输入整形: 0123456789
	return function(scope, element) {
		return (element.attr('bc-int-type') == 'true' || $commons.getValue(scope, element.attr('bc-int-type'))) && element.bind('keypress', function(event) { // keypress 事件处理
			var keyCode = event.which;
			if (!(keyCode >= 48 && keyCode <= 57) || (keyCode == 13)) {
				event.preventDefault();
				return false;
			} else {
				return true;
			}
		}).bind('paste', function() { // CTR+V 事件处理
			element.val(element.val().replace(/\D|^0/g, ''));
		}).css('ime-mode', 'disabled'); //CSS设置输入法不可用
	};
}).directive('bcNumberType', function() { // 只能输入数字: 0123456789.
	return function(scope, element) {
		return element.bind('keypress', function(event) { // keypress 事件处理
			var keyCode = event.which;
			if (keyCode == 46) {
				return $(this).val().indexOf('.') == -1;
			} else {
				return (keyCode >= 48 && keyCode <= 57) || (keyCode == 13);
			}
		}).bind('paste', function() { // CTR+V 事件处理
			$(this).val($(this).val().replace(/\D|^0/g, ''));
		}).css('ime-mode', 'disabled'); //CSS设置输入法不可用
	};
}).directive('bcMaxLength', function($commons) { // 输入字符长度限制，中文算两个字节
	return function(scope, element) {
		var max = parseInt($commons.getValue(scope, element.attr('bc-max-length')) || element.attr('bc-max-length'));
		if (max > 0) {
			return element.bind('keypress', function(event) { // keypress 事件处理
				var len = 0, val = $commons.getValue(scope, element.attr('ng-model')), keyCode = event.which;
				for (var i = 0; val && i < val.length; i++) {
					if (val[i].match(/[^\x00-\xff]/ig) != null) { //全角
						len += 2;
					} else {
						len += 1;
					}
				}
				if (!((len < max) || (keyCode == 13))) {
					event.preventDefault();
					return false;
				} else {
					return true;
				}
			}).bind('paste', function() { // CTR+V 事件处理
				var len = 0, val = element.attr('value'), valToUse = '';
				for (var i = 0; i < val.length; i++) {
					if (val[i].match(/[^\x00-\xff]/ig) != null) { //全角
						len += 2;
					} else {
						len += 1;
					}

					if (len > max) { break; }

					valToUse += val[i];
				}
				element.attr('value', valToUse);
			}).css('ime-mode', 'disabled'); //CSS设置输入法不可用
		}
	};
}).directive('bcIframe', function($timeout, $commons) {
	return {
		restrict: 'A',
		link: function(scope, element, attrs) {
		    var options = $commons.getOptions(scope, attrs['bcIframe']);
			options == undefined && (options = {});
			options.top == undefined && (options.top = 0);
			options.bottom == undefined && (options.bottom = 0);
			element.css({ width: '100%', top: options.top + 'px', position: 'absolute' });
			element[0].height = Math.max(document.body.scrollHeight, document.body.clientHeight) - options.top - options.bottom;
		}
	};
}).directive('bcBindHtml', function($timeout, $commons) {
    return {
		restrict: 'A',
		link: function (scope, element, attrs) {
            scope.$watch(attrs['bcBindHtml'], function(html) {
                element.empty();
                html && (html = html.replace(/\n/g, '<br/>')) && element.append(html);
            });
		}
    };
}).directive('bcUeditor', function($timeout, $commons) { // ueditor.all.min.js
    return {
		restrict: 'A',
		link: function (scope, element, attrs) {
		    var options = $commons.getOptions(scope, attrs['bcUeditor']), id = 'ueditor' + new Date().getTime() + '' + Math.floor(Math.random() * 10000 + 1);
		    element.append('<script id="' + id + '" type="text/plain" style="width: 100%;"></script>');
            scope.$watch(options.model, function(val) {
                !scope[id] && (scope[id] = UE.getEditor(id, options.config)) && (scope[id].addListener('contentChange', function() { $commons.setValue(scope, options.model, scope[id].getContent()) }));
                val && scope[id].addListener('ready', function() { scope[id].setContent(val) });
            });
            scope.$on('$destroy', function() { scope[id] && scope[id].destroy() });
		}
    };
}).directive('bcUpload', function ($timeout, $commons, $state) {
	return {
		restrict: 'A',
		link: function (scope, element, attrs) {
			// 参数选项
			var options = $commons.getOptions(scope, attrs['bcUpload']);
			// 上传的验证方法
			if (options.validator == 'image') {
			    options.validator = function(media) {
			        var image = media.type.match('image.*');
			        !image && $commons.alert('请选择图片');
			        return image;
			    }
			} else if (!options.validator || !$.isFunction(options.validator)) {
			    // 默认不验证
                options.validator = function() { return true };
			}
			// 上传回调方法
			var callback = options.upcallback;
			options.upcallback = function(media, value) {
				var model = $commons.getValue(scope, options.model);
				model && $.isPlainObject(model) ? angular.extend(model, value) : $commons.setValue(scope, options.model, value);
				if (options.preview) {
					var reader = new FileReader();
					reader.onload = function (event) {
						scope.$apply(function () { $commons.setValue(scope, options.preview, event.target.result) })
					}
					reader.readAsDataURL(media);
				}
				callback && scope[callback](media, value);
			};
			// 上传方法
            if (typeof options.uploader == 'string' && options.uploader != 'avuploader') { // 默认的上传
                var url = options.uploader;
				options.uploader = function(media) {
					var data = new FormData();
					data.append("multipartFile", media);
					$commons.upload({
						url: url,
						data: data,
						success: function(status) {
						    status.code === 200 ? options.upcallback(media, status) : $commons.alert(status.message);
						},
						errorText: '服务请求错误'
					});
				};
			} else if (options.uploader == 'avuploader') { // leancloud 的上传
				options.uploader = function(media) {
					var progressbar = $commons.progressbar().show(), avfile = new AV.File(media.name, media);
					avfile.save().then(function () {
						options.upcallback(media, avfile._url);
						progressbar.hide();
					}, function (error) {
						progressbar.hide();
						$commons.alert('上传失败: ' + error.message);
					});
				};
			} else {
                throw 'bc-upload options.uploader undefined.';
            }
			element.bind('change', function (event) {
				var media = event.currentTarget.files[0];
				media && options.validator(media) && options.uploader(media);
			});
		}
	}
}).directive('bcDatetimePicker', function($timeout, $commons) { // https://github.com/Eonasdan/bootstrap-datetimepicker
    return function(scope, element, attrs) {
        var input = element.find('input'), options = $commons.extend({locale: 'zh-CN'}, $commons.getOptions(scope, attrs['bcDatetimePicker']));
        return $timeout(function() {
            var optionsToUse = $commons.extend({}, options);
            input.val($commons.getValue(scope, optionsToUse.model || ''));
            delete optionsToUse.model;
            element.datetimepicker(optionsToUse);
            element.on("dp.change", function (e) { scope.$apply(function () { $commons.setValue(scope, options.model, input.val()) }) });
            scope.$watch(options.model, function(val) { input.val(val) });
        }, 300);
    }
}).directive('bcRangeSlider', function($timeout, $commons) { // https://github.com/IonDen/ion.rangeSlider
    return function(scope, element, attrs) {
        var options = $commons.getOptions(scope, attrs['bcRangeSlider']);
        return $timeout(function() {
            var range = $commons.getValue(scope, options.model || '') + '';
            if (range) {
                var index = range.indexOf(';');
                if (index != -1) {
                    options.from = range.substr(0, index);
                    options.to = range.substr(index + 1, range.length);
                } else {
                    options.from = range;
                }
            }
            element.ionRangeSlider(options);
            element.bind('change', function (e) { $commons.setValue(scope, options.model, element.val()) });
        }, 300);
    };
}).directive('bcDictInput', function($timeout, $commons) { // https://github.com/hotoo/pinyin
    return {
        restrict: 'E',
        template: '<div class="bc-dict-select-group">' +
            '<div class="input-group">' +
                '<input type="text" class="form-control input-sm"></input>' +
                '<span class="input-group-addon"><i class="ion-ios-keypad"></i></span>' +
            '</div>' +
            '<div class="bc-popup1">' +
                '<div class="bc-content"></div>' +
            '</div>' +
            '<div class="bc-popup2">' +
                '<i class="ion-checkmark-circled"></i>' +
                '<ul class="nav nav-pills"></ul>' +
                '<div class="bc-contents"></div>' +
            '</div>' +
        '</div>',
        replace: true,
        transclude: true,
        link: function (scope, element, attrs) {
            var options = $commons.getOptions(scope, attrs['options']) || {},
                mode = 'input',
                model = $commons.getValue(scope, options.model),
                inputgroup = element.find('div.input-group'),
                input = inputgroup.children('input'),
                // bc-dict-input
                cached1 = {},
                popup1 = element.children('div.bc-popup1'),
                content1 = popup1.children('div.bc-content'),
                render1 = function(data) {
                    content1.empty();
                    var joins = [];
                    for (var i = 0; i < data.result.length; i++) {
                        cached1[data.result[i].id] = data.result[i];
                        joins.push('<div xid="' + data.result[i].id + '" class="bc-item bc-ellipsis">');
                        joins.push(data.result[i].name);
                        joins.push('</div>');
                    }
                    data.total > data.result.length && joins.push('<div class="bc-item bc-more">...</div>');
                    content1.append(joins.join(''));
                    content1.children('.bc-item').click(function(e) {
                        options.timer1 && clearTimeout(options.timer1) && (delete options.timer1);
                        content1.children('.bc-item').removeClass('active');
                        $(e.target).closest('.bc-item').addClass('active');
                        input.blur();
                    });
                    popup1.width(inputgroup.width() - 12).show();
                },
                // bc-dict-select
                cached2 = {},
                index2 = options.index2 || 0,
                parent2 = { type: options.dict.type, cc: 1, uname: options.dict.uname },
                popup2 = element.children('div.bc-popup2'),
                tabs2 = popup2.children('ul.nav'),
                contents2 = popup2.children('div.bc-contents'),
                confirm2 = popup2.children('.ion-checkmark-circled'),
                search2 = function() {
                    !options.tabs[index2].offset && (options.tabs[index2].offset = 0);
                    !options.tabs[index2].limit && (options.tabs[index2].limit = 100);
                    $commons.post({
                        url: '/api/dict/search?offset=' + options.tabs[index2].offset + '&limit=' + options.tabs[index2].limit,
                        data: parent2,
                        success: function(status) {
                            if (status.code === 200) {
                                var pagination = status.data;
                                var cid = tabs2.children('li.bc-tab').eq(index2).attr('cid'), content = contents2.children('div.bc-content').eq(index2).empty(), joins = [];
                                if (options.tabs[index2].group) {
                                    var names = [], count = parseInt(pagination.result.length / options.tabs[index2].group, 10), groups = [];
                                    for (var i = 0; i < pagination.result.length; i++) { (cached2[pagination.result[i].id] = pagination.result[i]) && (cached2[pagination.result[i].name] = pagination.result[i]) && names.push(pagination.result[i].name) }
                                    names = names.sort(pinyin.compare);
                                    for (var i = 0; i < names.length; i++) {
                                        var current = parseInt(i / count, 10);
                                        !groups[current] && (groups[current] = []);
                                        groups[parseInt(i / count, 10)].push(cached2[names[i]]);
                                    }
                                    for (var i = 0; i < groups.length; i++) {
                                        var start = pinyin(groups[i][0].name.substr(0, 1), { style: pinyin.STYLE_FIRST_LETTER })[0][0].toUpperCase(), end = pinyin(groups[i][groups[i].length - 1].name.substr(0, 1), { style: pinyin.STYLE_FIRST_LETTER })[0][0].toUpperCase();
                                        joins.push('<div class="bc-group row">');
                                            joins.push('<div class="col-xs-2 bc-title">');
                                            joins.push(start != end ? start + '-' + end : start);
                                            joins.push('</div>');
                                            joins.push('<div class="col-xs-10 bc-content">');
                                            for (var j = 0; j < groups[i].length; j++) { joins.push('<div xid="' + groups[i][j].id + '" cc="' + groups[i][j].cc + '" class="bc-item' + (cid == groups[i][j].id ? ' active' : '') + '">' + groups[i][j].name + '</div>') }
                                            joins.push('</div>');
                                        joins.push('</div>');
                                    }
                                } else {
                                    for (var i = 0; i < pagination.result.length; i++) { (cached2[pagination.result[i].id] = pagination.result[i]) && joins.push('<div xid="' + pagination.result[i].id + '" cc="' + pagination.result[i].cc + '" class="bc-item' + (cid == pagination.result[i].id ? ' active' : '') + '">' + pagination.result[i].name + '</div>') }
                                }
                                content.append(joins.join(''));
                                var items = content.find('div.bc-item');
                                items.click(function(e) {
                                    content.find('div.bc-item').removeClass('active');
                                    var item = $(e.target).closest('div.bc-item').addClass('active');
                                    tabs2.children('.bc-index-' + index2).attr('cid', item.attr('xid'));
                                    if (index2 < options.tabs.length && parseInt(item.attr('cc'), 10) > 0) {
                                        tabs2.children('.bc-index-' + (index2 + 1)).attr('pid', item.attr('xid')).click();
                                    } else {
                                        confirm2.click();
                                    }
                                });
                                (items.length == 1) && items.eq(0).click();
                            } else {
                                $commons.alert(status.message);
                            }
                        },
                        errorText: '服务请求错误'
                    });
                };
            // commons
            options.placeholder && input.attr('placeholder', options.placeholder);
            (options.mode == 'input') && inputgroup.removeClass('input-group').children('.input-group-addon').hide();
            // bc-dict-input
            if (!options.mode || options.mode == 'input') {
                input.focus(function() { (mode = 'input') && popup2.hide() });
                input.on('keypress', function(event) {
                    var keyCode = event.which;
                    if (!event.shiftKey
                        && !event.ctrlKey
                        && !event.altKey
                        && keyCode == 13
                        && popup1.is(':visible')) {
                        content1.children('.bc-item.active').length == 1 && input.blur();
                        return false;
                    }
                }).on('keyup', function(event) {
                    var keyCode = event.which, text = $.trim(input.val());
                    if (options.dict.uname == 'true') {
                        var texts = text.split('/');
                        text = texts[texts.length - 1];
                    }
                    if (!event.shiftKey
                        && !event.ctrlKey
                        && !event.altKey
                        && keyCode != 13
                        && keyCode != 16
                        && keyCode != 17
                        && keyCode != 18
                        && keyCode != 33
                        && keyCode != 34
                        && keyCode != 37
                        && keyCode != 38
                        && keyCode != 39
                        && keyCode != 40
                        && text) {
                        !cached1[text] ? $commons.post({
                            url: '/api/dict/search?offset=0&limit=20',
                            data: { type: options.dict.type, uname: options.dict.uname, name: text },
                            success: function(status) {
                                status.code === 200 ? render1(cached1[text] = status.data) : $commons.alert(status.message);
                            },
                            errorText: '服务请求错误'
                        }) : render1(cached1[text]);
                    } else if (!event.shiftKey
                        && !event.ctrlKey
                        && !event.altKey
                        && keyCode == 38
                        && popup1.is(':visible')) {
                        var active = content1.children('.bc-item.active'), has = active.length, prev;
                        content1.children('.bc-item').removeClass('active');
                        has == 0 ? content1.children('.bc-item:last').addClass('active') : (prev = active.prev()).length == 0 ? active.addClass('active') : prev.addClass('active');
                    } else if (!event.shiftKey
                        && !event.ctrlKey
                        && !event.altKey
                        && keyCode == 40
                        && popup1.is(':visible')) {
                        var active = content1.children('.bc-item.active'), has = active.length, next;
                        content1.children('.bc-item').removeClass('active');
                        has == 0 ? content1.children('.bc-item:first').addClass('active') : (next = active.next()).length == 0 ? active.addClass('active') : next.addClass('active');
                    }
                }).on('blur', function(event) {
                    options.timer1 = setTimeout(function() {
                        var text = $.trim(input.val()), active, modelToUse, confirm = function(modelToUse) {
                            model = modelToUse;
                            model && model.id && (cached1[model.id] = model);
                            model && model.id && (cached2[model.id] = model);
                            $commons.setValue(scope, options.model, model);
                            input.val(model ? (model.uname || model.name) : '');
                            popup1.hide();
                        };
                        if (options.dict.uname == 'true') {
                            var texts = text.split('/');
                            text = texts[texts.length - 1];
                        }
                        if (popup1.is(':visible') && (active = content1.children('.bc-item.active')).length == 1) {
                            modelToUse = cached1[active.attr('xid')];
                        } else if (text && cached1[text] && cached1[text].total == 1) {
                            modelToUse = cached1[text].result[0];
                        } else if (text && model && model.id && cached1[model.id]) {
                            modelToUse = cached1[model.id];
                        }
                        options.confirm ? $commons.getValue(scope, options.confirm)(text, modelToUse, confirm) : confirm(modelToUse);
                    }, 200);
                });
                scope.$watch(options.model, function(modelToUse) {
                    if (mode == 'input' && (model = modelToUse)) {
                        !input.is(':focus') && (model.uname || model.name) && input.val(model.uname || model.name);
                        model.id && (cached1[model.id] = model);
                        model.id && (cached2[model.id] = model);
                    }
                });
            }
            // bc-dict-select
            if (!options.mode || options.mode == 'select') {
                for (var i = 0; i < options.tabs.length; i++) {
                    tabs2.append('<li index="' + i + '" class="bc-tab bc-index-' + i + '"><a>' + options.tabs[i].name + '</a></li>');
                    contents2.append('<div index="' + i + '" class="bc-content bc-index-' + i + '"></div>');
                }
                tabs2.children('li.bc-tab').click(function(e) {
                    tabs2.children('li.bc-tab').removeClass('active');
                    var tab = $(e.target).closest('li.bc-tab').addClass('active');
                    contents2.children('div.bc-content').hide().eq(index2 = parseInt(tab.attr('index'), 10)).show();
                    (parent2.pid = tab.attr('pid')) && search2();
                }).eq(0).attr('pid', options.dict.id);
                element.find('div.input-group .input-group-addon').click(function(e) {
                    (mode = 'select') && popup1.hide();
                    var group = $(e.target).closest('div.input-group');
                    if (popup2.is(':hidden')) {
                        popup2.width(inputgroup.width() - 12).show();
                        model && model.id ? $commons.post({
                            url: '/api/dict/get',
                            data: { type: options.dict.type, id: model.id, xpath: 'true' },
                            success: function(status) {
                                if (status.code === 200) {
                                    if (status.data.xpath && (status.data.xpaths = status.data.xpath.substr(1, status.data.xpath.length).split('/'))) {
                                        options.dict.id == 'null' && status.data.xpaths.unshift('null');
                                        for (var i = $.inArray(options.dict.id, status.data.xpaths), ts = tabs2.children('li.bc-tab'); i < status.data.xpaths.length && i < ts.length; i++) {
                                            (status.last = ts.eq(i)).attr('pid', status.data.xpaths[i]);
                                            i + 1 < status.data.xpaths.length && status.last.attr('cid', status.data.xpaths[i + 1]);
                                        }
                                    } else {
                                        status.last = tabs2.children('li.bc-tab.bc-index-' + index2);
                                    }
                                    status.last.click();
                                } else {
                                    $commons.alert(status.message);
                                }
                            },
                            errorText: '服务请求错误'
                        }) : tabs2.children('li.bc-tab.bc-index-' + index2).click();
                    } else {
                        popup2.hide();
                    }
                });
                element.on('mouseenter', function() {
                    options.timer2 && clearTimeout(options.timer2) && (delete options.timer2);
                }).on('mouseleave', function() {
                    popup2.is(':visible') && (options.timer2 = setTimeout(function() { popup2.fadeOut() }, 1800));
                });
                confirm2.click(function(e) {
                    var text = $.trim(input.val()), confirm = function(modelToUse) {
                        model = modelToUse;
                        model && model.id && (cached1[model.id] = model);
                        model && model.id && (cached2[model.id] = model);
                        $commons.setValue(scope, options.model, model);
                        input.val(model ? (model.uname || model.name) : '');
                        popup2.hide();
                    }, cid = '';
                    if (options.dict.uname == 'true') {
                        var texts = text.split('/');
                        text = texts[texts.length - 1];
                    }
                    for (var i = index2; i >= 0 && !cid; i--) { cid = contents2.children('div.bc-content').eq(i).find('.bc-item.active').attr('xid') }
                    options.confirm ? $commons.getValue(scope, options.confirm)(text, cid ? cached2[cid] : null, confirm) : confirm(cid ? cached2[cid] : null);
                });
                scope.$watch(options.model, function(modelToUse) { mode == 'select' && (model = modelToUse) && model.id && input.val(model.uname || model.name) });
            }
        }
    };
}).directive('bcPagination', function($timeout, $commons) {
    return {
        restrict: 'E',
        template: '<nav class="bc-pagination">' +
            '<ul class="pagination">' +
                '<li class="bc-first"><a>&laquo;</a></li>' +
                '<li class="bc-pre"><a>&lsaquo;</a></li>' +
                '<li class="bc-next"><a>&rsaquo;</a></li>' +
                '<li class="bc-last"><a>&raquo;</a></li>' +
                '<li class="bc-cli">' +
                    '<a>' +
                        '<input type="text" class="form-control input-sm bc-cur">' +
                    '</a>' +
                '</li>' +
                '<li class="bc-go"><a>go</a></li>' +
            '</ul>' +
        '</nav>',
        replace: true,
        transclude: true,
        link: function (scope, element, attrs) {
            var options = $commons.getOptions(scope, attrs['options']) || {},
                model = $commons.getValue(scope, options.model), pager = {},
                pcount = function() { return parseInt(model.total / model.limit, 10) + (model.total % model.limit > 0 ? 1 : 0); },
                first = element.find('.bc-first'),
                pre = element.find('.bc-pre'),
                next = element.find('.bc-next'),
                last = element.find('.bc-last'),
                cli = element.find('.bc-cli'),
                cur = element.find('.bc-cur'),
                go = element.find('.bc-go');
            first.click(function() { pager.current = 1; go.click(); });
            pre.click(function() { pager.current--; go.click(); });
            next.click(function() { pager.current++; go.click(); });
            last.click(function() { pager.current = model ? pcount() : 1; go.click(); });
            cur.keyup(function(event) { event.which == 13 && (pager.current = parseInt(this.value, 10)) && go.click(); });
            go.click(function() { $commons.getValue(scope, options.action)((pager.current - 1) * (model ? model.limit : 0)) });
            scope.$watch(options.model, function(modelToUse) {
                if (model = modelToUse) {
                    if (model.offset == 0 || model.result.length > 0) {
                        pager.current = (model.offset / model.limit) + 1;
                        cur.val(pager.current);
                        element.find('.bc-jump').remove();
                        !options.range && (options.range = parseInt(model.limit / 2, 10));
                        var pc = pcount(), start = Math.max(pager.current - (options.range / 2), 0), end = Math.min(start + options.range, pc);
                        while (pc > options.range && end - start < options.range && start > 0) { start--; }
                        while (pc > options.range && end - start < options.range && end < pc) { end++; }
                        for (var i = start; i < end; i++) { next.before('<li class="bc-jump" jump="' + (i + 1) + '"><a>' + (i + 1) + '</a></li>'); }
                        element.find('.bc-jump')
                            .each(function() { (this.attributes.jump.value == pager.current) && (this.className = this.className + ' active'); })
                            .click(function() { pager.current = parseInt(this.attributes.jump.value, 10); go.click(); });
                        if (pc > end - start) {
                            if (pager.current == 1) {
                                first.hide() && pre.hide() && next.show() && last.show();
                            } else if (pager.current == pc) {
                                first.show() && pre.show() && next.hide() && last.hide();
                            } else {
                                first.show() && pre.show() && next.show() && last.show();
                            }
                            cli.show() && go.show();
                            pager.current < pc ? last.show() : last.hide();
                        } else {
                            first.hide() && pre.hide() && next.hide() && last.hide() && cli.hide() && go.hide();
                        }
                        model.offset == 0 && model.result.length < model.limit ? element.hide() : element.show();

                    } else {
                        pre.click();
                    }
                } else {
                    element.hide();
                }
            });
        }
    };
}).factory('$commons', function($http, $timeout, $locale, $commonsPlugin) {
    var $commons; // for this
	return $commons = angular.extend({
	    getOptions: function(scope, attr) {
	        return (attr = $commons.trim(attr)) && attr.indexOf('{') == 0 ? eval('(' + attr + ')') : $commons.getValue(scope, attr);
	    },
		getValue: function(root, name) {
			if (root) {
				if (name) {
					var A = name.indexOf('['), B = name.indexOf('.');
					if (A == 0) { // [n] || [n].*
						var end = name.indexOf(']'), pname = name.substring(1, end), cname = name.substring(end + 1);
						cname.substring(0, 1) == '.' && (cname = cname.substring(1));
						!isNaN(pname) && (pname = parseInt(pname, 10)); // 假如是数字字符串，转换为数字类型
						return cname ? $commons.getValue(root[pname], cname) : root[pname];
					}  else if (A != -1 && B != -1) {
						if (A < B) { // *[n].*
							var pname = name.substring(0, A), cname = name.substring(A);
							return $commons.getValue(root[pname], cname);
						} else { // *.*[n]
							var pname = name.substring(0, B), cname = name.substring(B + 1);
							return $commons.getValue(root[pname], cname);
						}
					} else if (A == -1 && B != -1) { // *.*
						var pname = name.substring(0, B), cname = name.substring(B + 1);
						return $commons.getValue(root[pname], cname);
					} else if (A != -1 && B == -1) { // *[n]
						var pname = name.substring(0, A), cname = name.substring(A);
						return $commons.getValue(root[pname], cname);
					} else {
						return root[name];
					}
				}
			}
		},
		setValue: function (root, name, value) {
			if (root) {
				if (name) {
					var A = name.indexOf('['), B = name.indexOf('.');
					if (A == 0) { // [n] || [n].*
						var end = name.indexOf(']'), pname = name.substring(1, end), cname = name.substring(end + 1);
						cname.substring(0, 1) == '.' && (cname = cname.substring(1));
						!isNaN(pname) && (pname = parseInt(pname, 10)); // 假如是数字字符串，转换为数字类型
						return cname ? $commons.setValue(root[pname] || (root[pname] = {}), cname, value) : (root[pname] = value);
					}  else if (A != -1 && B != -1) {
						if (A < B) { // *[n].*
							var pname = name.substring(0, A), cname = name.substring(A);
							return $commons.setValue(root[pname] || (root[pname] = {}), cname, value);
						} else { // *.*[n]
							var pname = name.substring(0, B), cname = name.substring(B + 1);
							return $commons.setValue(root[pname] || (root[pname] = {}), cname, value);
						}
					} else if (A == -1 && B != -1) { // *.*
						var pname = name.substring(0, B), cname = name.substring(B + 1);
						return $commons.setValue(root[pname] || (root[pname] = {}), cname, value);
					} else if (A != -1 && B == -1) { // *[n]
						var pname = name.substring(0, A), cname = name.substring(A);
						return $commons.setValue(root[pname] || (root[pname] = {}), cname, value);
					} else {
						root[name] = value;
					}
				}
			}
		},
		nameOf: function(array, name, value, dest) {
			if (name != undefined && value == undefined) {
				value = name.name;
				name = 'name';
			}
			for (var i = 0; array && name && value && i < array.length; i++) {
				if (array[i][name] == value) {
					if (dest == undefined) {
						return array[i];
					} else {
						dest.push(array[i]);
					}
				}
			}
			return dest;
		},
		assign: function(array, srcname, destname, value) {
			for (var i = 0; array && srcname && destname && i < array.length; i++) {
				array[i][srcname] != undefined && (value != undefined ? (array[i][destname] = array[i][srcname] = value) : (array[i][destname] = array[i][srcname]));
			}
		},
        toggle: function(elements, element) {
            if ($.isArray(elements) && !$.isFunction(element)) {
                for (var i = 0; i < elements.length; i++) { elements[i].checked = false || (elements[i].id == element.id && (elements[i].checked = true)) }
                element.checked = true;
            } if (!$.isFunction(elements) && $commons.type(element) == 'string') {
                $commons.setValue(elements, element, !$commons.getValue(elements, element));
            } else if (!$.isFunction(elements)) {
                elements.checked = !elements.checked;
            }
        },
        toggles: function(elements, options) {
            if (options && Array.isArray(options)) {
                for (var i = 0; elements && i < elements.length; i++) { for (var j = 0; options && j < options.length; j++) { elements[i].id == options[j].id && (elements[i].checked = true) } }
            } else {
                var elementsToUse = [];
                for (var i = 0; i < elements.length; i++) { elements[i].checked && elementsToUse.push(elements[i]) }
                return elementsToUse;
            }
        },
		wsize: function() {
			var height = 0, width = 0;
			if ($commons.type(window.innerWidth) == 'number') {
				height = window.innerHeight;
				width = window.innerWidth;
			} else if (document.documentElement && (document.documentElement.clientWidth || document.documentElement.clientHeight)) {
				height = document.documentElement.clientHeight;
				width = document.documentElement.clientWidth;
			} else if (document.body && (document.body.clientWidth || document.body.clientHeight)) {
				height = document.body.clientHeight;
				width = document.body.clientWidth;
			}
			return { width: width, height: height};
		},
		loading: function(func) {
			var progressbar = $commons.progressbar().show();
			$timeout(func, 0)['finally'](function() { progressbar.hide() });
		},
		setupRef: function(root, object, attr, value) {
			(arguments.length == 1) && (value = root);
			for (sa in value) {
				if (sa == '$ref') {
					object[attr] = eval('root' + value[sa].substring(1));
					break;
				} else if (angular.isObject(value[sa])) {
					$commons.setupRef(root, value, sa, value[sa]);
				}
			}
			return root;
		},
		cformat: function(fmt, object) {
			for (var i = 2; i < arguments.length; i++) {
				var name = arguments[i], date = object ? object[name] : null, value = $commons.dformat(fmt, date);
				value && (object[name] = value);
			}
		},
		dformat: function(fmt, date) {
			return date && date.format ? date.format(fmt) : date;
		},
		dparse: function(fmt, text, dd) {
			if (text && text.length == 'yyyy-MM'.length && fmt == 'yyyy-MM') {
				var date = new Date();
				date.setFullYear(parseInt(text.substring(0, 4), 10), parseInt(text.substring(5, 7), 10) - 1, 1);
				return date;
			} else if (text && text.length == 'yyyy-MM-dd'.length && fmt == 'yyyy-MM-dd') {
                var date = new Date();
                date.setFullYear(parseInt(text.substring(0, 4), 10), parseInt(text.substring(5, 7), 10) - 1, parseInt(text.substring(8, 10), 10));
                return date;
            } else if (text && text.length == 'yyyy-MM-dd HH:mm:ss'.length && fmt == 'yyyy-MM-dd HH:mm:ss') {
                var date = new Date();
                date.setFullYear(parseInt(text.substring(0, 4), 10), parseInt(text.substring(5, 7), 10) - 1, parseInt(text.substring(8, 10), 10));
                date.setHours(parseInt(text.substring(11, 13), 10));
                date.setMinutes(parseInt(text.substring(14, 16), 10));
                date.setSeconds(parseInt(text.substring(17, 19), 10));
                return date;
            }
			return dd;
		},
        localhost: function(url) {
            var localhost = url || window.location.href, index = localhost.indexOf('//');
            localhost = localhost.substr(index + 2);
            index = localhost.indexOf('/');
            localhost = localhost.substr(0, index);
            index = localhost.indexOf(':');
            (index != -1) && (localhost = localhost.substr(0, index));
            return localhost == '127.0.0.1' || localhost == 'localhost';
        },
        params: function(url) {
            var params = url || window.location.href, index = params.indexOf('?');
            (index != -1) && (params = params.substr(index + 1, params.length));
            ((index = params.indexOf('#')) != -1) && (params = params.substr(0, index));
            var tmp = params.split('&'), params = {};
            for (var i = 0; tmp && i < tmp.length; i++) {
                var index = tmp[i].indexOf('='), name = tmp[i].substr(0, index), value = tmp[i].substr(index + 1, tmp[i].length);
                params[name] = value;
            }
            return params;
        },
		http: function(options) {
			var progressbar = $commons.progressbar().show();
			$http(options).success(function() {
				progressbar.hide();
				options.success && options.success.apply(this, arguments);
			}).error(function() {
				progressbar.hide();
				options.errorText && $commonsPlugin.alert(options.errorText);
				options.error && options.error.apply(this, arguments);
			});
		},
		post: function(options) {
			$commons.http(angular.extend({
				method: 'post',
				responseType: 'json',
				cache: false
			}, options));
		},
		get: function(options) {
			$commons.http(angular.extend({
				method: 'get',
				responseType: 'json',
				cache: false
			}, options));
		},
		delete: function(options) {
			$commons.http(angular.extend({
				method: 'delete',
				responseType: 'json',
				cache: false
			}, options));
		},
		upload: function(options) {
			$commons.http(angular.extend({
				method: 'post',
				headers: { 'Content-Type': undefined },
				transformRequest: angular.identity,
				responseType: 'json'
			}, options));
		},
		toast: function(options) {
			var optionsToUse = angular.extend({
				closeButton: !0,
	            positionClass: 'toast-bottom-right',
	            timeOut: '3000',
	            level: 'info'
			}, options);
			toastr.options = optionsToUse;
			toastr[optionsToUse.level](optionsToUse.text);
		}
	}, $commonsPlugin)
})

// 对Date的扩展，将 Date 转化为指定格式的String   
// 月(M)、日(d)、小时(h)、分(m)、秒(s)、季度(q) 可以用 1-2 个占位符，   
// 年(y)可以用 1-4 个占位符，毫秒(S)只能用 1 个占位符(是 1-3 位的数字)   
// 例子：
// (new Date()).Format("yyyy-MM-dd hh:mm:ss.S") ==> 2006-07-02 08:09:04.423   
// (new Date()).Format("yyyy-M-d h:m:s.S")      ==> 2006-7-2 8:9:4.18   
Date.prototype.format = function(fmt) { //author: meizz   
	var o = {
		"M+": this.getMonth() + 1, //月份
		"d+": this.getDate(), //日
		"h+": this.getHours(), //小时
		"m+": this.getMinutes(), //分
		"s+": this.getSeconds(), //秒
		"q+": Math.floor((this.getMonth() + 3) / 3), //季度
		"S": this.getMilliseconds() // 毫秒
	};
	if (/(y+)/.test(fmt)) {
		fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
	}
	for (var k in o) {
		if (new RegExp("(" + k + ")").test(fmt)) {
			fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
		}
	}
	return fmt;
}
