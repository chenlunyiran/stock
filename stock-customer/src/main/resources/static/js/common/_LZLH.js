function LZLH() {
	function debug(msg) {
		// alert(msg);
		console.info("[debug] : " + msg);
		$("#log").append(msg);
	}
	function isWn() {
		return (typeof _LZLH) !== "undefined";
	}
	function isIosApp() {
		return TT.sys.versions.ios && isWn();
	}
	function isAndApp() {
		return TT.sys.versions.android && isWn();
	}
	var isApp = isAndApp() || isIosApp();
	function version() {
		if (isWn()) {
			try {
				var v = navigator.userAgent.split(" ")[0].split("/")[1];
				var vv = v.split(".");
				return parseInt(vv[0]) * 100 + parseInt(vv[1]) * 10
						+ parseInt(vv[2]);
			} catch (e) {
				debug("error:" + navigator.userAgent)
			}
		}
		return 0;
	}
	debug("isApp : " + isApp);

	if (typeof _LZLH !== "undefined") {
		debug("_LZLH: " + JSON.stringify(_LZLH))
	}
	window._LZLH_HANDLE = window._LZLH_HANDLE || [];
	function handle(callback) {
		var key = "_cb_" + new Date().getTime();
		_LZLH_HANDLE[key] = function(data) {
			debug(data);
			if (typeof callback !== "function") {
				debug("callback must function");
				return;
			}
			callback(data);
			_LZLH_HANDLE[key] = null;
		}
		return "window._LZLH_HANDLE." + key;
	}

	return {
		getUserInfo : function(params) {
			params = params || {};
			if (isApp) {
				if (typeof params.callback === "function") {
					params.cb = handle(params.callback);
				}
				_LZLH.getUserInfo(JSON.stringify(params));
			}
		},
		getToken : function(params) {
			params = params || {};
			if (isApp) {
				if (typeof params.callback === "function") {
					params.cb = handle(params.callback);
				}
				_LZLH.getToken(JSON.stringify(params));
			}
		},
		toRegist : function(params) {
			params = params || {};
			if (isApp) {
				if (typeof params.callback === "function") {
					params.cb = handle(params.callback);
				}
				_LZLH.toRegist(JSON.stringify(params));
			}
		},
		toLogin : function(params) {
			params = params || {};
			if (isApp) {
				if (typeof params.callback === "function") {
					params.cb = handle(params.callback);
				}
				_LZLH.toLogin(JSON.stringify(params));
			}
		},
		toIndex : function(params) {
			params = params || {};
			if (isApp) {
				if (typeof params.callback === "function") {
					params.cb = handle(params.callback);
				}
				_LZLH.toIndex(JSON.stringify(params));
			}
		},
		toProList : function(params) {
			params = params || {};
			if (isApp) {
				if (typeof params.callback === "function") {
					params.cb = handle(params.callback);
				}
				_LZLH.toProList(JSON.stringify(params));
			}
		},
		toBondList : function(params) {
			params = params || {};
			if (isApp) {
				if (typeof params.callback === "function") {
					params.cb = handle(params.callback);
				}
				_LZLH.toBondList(JSON.stringify(params));
			}
		},
		toUser : function(params) {
			params = params || {};
			if (isApp) {
				if (typeof params.callback === "function") {
					params.cb = handle(params.callback);
				}
				_LZLH.toUser(JSON.stringify(params));
			}
		},
		toMore : function(params) {
			params = params || {};
			if (isApp) {
				if (typeof params.callback === "function") {
					params.cb = handle(params.callback);
				}
				_LZLH.toMore(JSON.stringify(params));
			}
		},
		toProX : function(params) {
			params = params || {};
			if (isApp) {
				if (typeof params.callback === "function") {
					params.cb = handle(params.callback);
				}
				_LZLH.toProX(JSON.stringify(params));
			}
		},
		toProXIn : function(params) {
			params = params || {};
			if (isApp) {
				if (typeof params.callback === "function") {
					params.cb = handle(params.callback);
				}
				_LZLH.toProXIn(JSON.stringify(params));
			}
		},
		toActQuan : function(params) {
			params = params || {};
			if (isApp) {
				if (typeof params.callback === "function") {
					params.cb = handle(params.callback);
				}
				_LZLH.toActQuan(JSON.stringify(params));
			}
		},
		toKeFu : function(params) {
			params = params || {};
			if (isApp) {
				if (typeof params.callback === "function") {
					params.cb = handle(params.callback);
				}
				_LZLH.toKeFu(JSON.stringify(params));
			}
		},
		toAssetRecord : function(params) {
			params = params || {};
			if (isApp) {
				if (typeof params.callback === "function") {
					params.cb = handle(params.callback);
				}
				_LZLH.toAssetRecord(JSON.stringify(params));
			}
		},
		
		toShare : function(params) {
			params = params || {};
			if (isApp) {
				if (typeof params.callback === "function") {
					params.cb = handle(params.callback);
				}
				_LZLH.toShare(JSON.stringify(params));
			}
		},
		shareConfig : function(params) {
			params = params || {};
			if (isApp) {
				if (typeof params.callback === "function") {
					params.cb = handle(params.callback);
				}
				_LZLH.shareConfig(JSON.stringify(params));
			}
		},
		toJxbAcc : function(params) {
			params = params || {};
			if (isApp) {
				if (typeof params.callback === "function") {
					params.cb = handle(params.callback);
				}
				_LZLH.toJxbAcc(JSON.stringify(params));
			}
		},
		toAppDown : function(params) {
			params = params || {};
			if (isApp) {
				if (typeof params.callback === "function") {
					params.cb = handle(params.callback);
				}
				_LZLH.toAppDown(JSON.stringify(params));
			}
		},
		toCopy : function(params) {
			params = params || {};
			if (isApp) {
				if (typeof params.callback === "function") {
					params.cb = handle(params.callback);
				}
				_LZLH.toCopy(JSON.stringify(params));
			}
		},
		toForgetPayPwd : function(params) {
			params = params || {};
			if (isApp) {
				if (typeof params.callback === "function") {
					params.cb = handle(params.callback);
				}
				_LZLH.toForgetPayPwd(JSON.stringify(params));
			}
		},
		toProjectDetail : function(params) {
			params = params || {};
			if (isApp) {
				if (typeof params.callback === "function") {
					params.cb = handle(params.callback);
				}
				_LZLH.toProjectDetail(JSON.stringify(params));
			}
		},
		toProjectSetSonDetail : function(params) {
			params = params || {};
			if (isApp) {
				if (typeof params.callback === "function") {
					params.cb = handle(params.callback);
				}
				_LZLH.toProjectSetSonDetail(JSON.stringify(params));
			}
		},
		toBondProjectDetail : function(params) {
			params = params || {};
			if (isApp) {
				if (typeof params.callback === "function") {
					params.cb = handle(params.callback);
				}
				_LZLH.toBondProjectDetail(JSON.stringify(params));
			}
		},
		toProjectXDetail : function(params) {
			params = params || {};
			if (isApp) {
				if (typeof params.callback === "function") {
					params.cb = handle(params.callback);
				}
				_LZLH.toProjectXDetail(JSON.stringify(params));
			}
		},
		toProjectInvest : function(params) {
			params = params || {};
			if (isApp) {
				if (typeof params.callback === "function") {
					params.cb = handle(params.callback);
				}
				_LZLH.toProjectInvest(JSON.stringify(params));
			}
		},
		toBondProjectInvest : function(params) {
			params = params || {};
			if (isApp) {
				if (typeof params.callback === "function") {
					params.cb = handle(params.callback);
				}
				_LZLH.toBondProjectInvest(JSON.stringify(params));
			}
		},
		toCloseWindow : function(params) {
			params = params || {};
			if (isApp) {
				if (typeof params.callback === "function") {
					params.cb = handle(params.callback);
				}
				_LZLH.toCloseWindow(JSON.stringify(params));
			}
		},
		showContactDialog : function(params) {
			params = params || {};
			if (isApp) {
				if (typeof params.callback === "function") {
					params.cb = handle(params.callback);
				}
				_LZLH.showContactDialog(JSON.stringify(params));
			}
		},
		toWebView : function(params) {
			params = params || {};
			if (isApp) {
				if (typeof params.callback === "function") {
					params.cb = handle(params.callback);
				}
				_LZLH.toWebView(JSON.stringify(params));
			}
		},
		support : function(funcName) {
			return true;
		},
		info : function() {
			var os = TT.sys.versions.ios ? "IOS" : "OTHER";
			os = TT.sys.versions.android ? "AND" : os;
			var soft = isWn() ? "woneng" : "OTHER";
			var soft = TT.isWeixin() ? "weixin" : soft;
			return {
				os : os,
				soft : soft,
				ver : version()
			}
		},
		saveImg : function(params) {
			params = params || {};
			if (isApp) {
				if (typeof params.callback === "function") {
					params.cb = handle(params.callback);
				}
				_LZLH.saveImg(JSON.stringify(params));
			}
		},
		toPDFViewer : function(params) {
			params = params || {};
			if (isApp) {
				if (typeof params.callback === "function") {
					params.cb = handle(params.callback);
				}
				_LZLH.toPDFViewer(JSON.stringify(params));
			}
		},
		toCashPage : function(params) {
			params = params || {};
			if (isApp) {
				if (typeof params.callback === "function") {
					params.cb = handle(params.callback);
				}
				_LZLH.toCashPage(JSON.stringify(params));
			}
		}
	}
}