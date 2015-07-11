var tr = tr || {};
//window.localStorage = window.localStorage || {};

// loadExtCss();

function log() {
	if (document.all)
		return;
	console.log.apply(console, arguments);
}

if (!document.all) {
	document.addEventListener('keydown', function(e) {
		if (e.which === 8 && e.target.tagName !== 'TEXTAREA' && e.target.tagName !== 'INPUT') {
			e.preventDefault();
		}
	});
}

// overrides //
var str = 'Ext.form.field.Text';

redefineFields('Ext.form.field.Text');
redefineFields('Ext.form.field.ComboBox');
redefineFields('Ext.form.field.TextArea');

function redefineFields(className) {
	Ext.define(className, {
		override : className,
		constructor : function(cfg) {
			cfg = cfg || {};
			var me = this;
			var isEng = cfg.isEng || me.isEng;
			var autoFocus = cfg.autoFocus || me.autoFocus;
			me.callParent(arguments);

			me.on({
				focus : function() {
					if(window.geokb) geokb.toggle(!isEng);
				}
			});
			if(autoFocus){
				me.on('afterrender', function(f){
					setTimeout(function(){
						f.focus();
					}, 300);
				});
			}

		}
	});
}

Ext.define('Ext.window.Window', {
	override: 'Ext.window.Window',
	constrain: true
});

Ext.define('Ext.form.Panel', {
	override : 'Ext.form.Panel',
	constructor : function(cfg) {
		cfg = cfg || {};
		var me = this;

		var fn = cfg.submitFn || me.submitFn;

		me.callParent(arguments);

		if (typeof fn === 'function') {
			me.addListener('afterrender', function() {
				me.keyNav = Ext.create('Ext.util.KeyNav', me.el, {
					enter : function(e) {
						if (me.getForm().isValid()) fn();
					}
				});
			});
		}
	}
});

Ext.define('Ext.grid.Panel', {
	override : 'Ext.grid.Panel',
	constructor : function(cfg) {
		cfg = cfg || {};
		var me = this;
		var countLabel = null;

		if (cfg.footer || me.footer) {
			countLabel = Ext.create('Ext.form.Label');

			me.bbar = [ {
				xtype : 'label',
				html : 'სულ: '
			}, countLabel ];
		}

		me.callParent(arguments);

		if (cfg.footer || me.footer) {
			me.getStore().on('datachanged', function() {
				var count = Math.max(me.getStore().getTotalCount(), me.getStore().getCount());
				countLabel.update(count.toString());
			});
		}
	}
});

// end overrides //

function loadExtCss() {
	var theme = localStorage.theme || 'ext-all-neptune';
	loadCss("http://extjs-public.googlecode.com/svn/tags/extjs-4.2.1/release/resources/css/" + theme + ".css");
	if (theme === 'ext-all-neptune')
		loadCss('./css/neptune.css');
	else
		loadCss('./css/classic.css');
}

function loadCss(url) {
	// var link = document.createElement("link");
	// link.setAttribute("rel", "stylesheet");
	// link.setAttribute("href", url);
	// link.setAttribute("type", "text/css");

	var link = '<link href = "' + url + '" type="text/css" rel="stylesheet" />';
	// document.getElementsByTagName('head')[0].appendChild(link);
	document.write(link);
}

function monitorEvents(obj) {
	Ext.util.Observable.capture(obj, function(evname, args) {
		log(evname, [ args ]);
	});
}

function deleteProperties(obj, arr) {
	for ( var i in arr)
		delete obj[arr[i]];
	return obj;
}

function getDataFromStore(store, exclude) {
	return getDataFromRecords(store.getRange(), exclude);
}

function getDataFromRecords(records, exclude) {
	var arr = [];
	exclude = exclude || [];
	for ( var i in records) {
		var data = Ext.clone(records[i].getData());
		for ( var j in exclude) {
			delete data[exclude[j]];
		}
		arr.push(data);
	}
	return arr;
}

function correctDates(obj, arr) {
	arr = arr || [];
	// var newObj = Ext.clone(obj);
	for ( var i in arr) {
		if (obj[arr[i]])
			obj[arr[i]] = new Date(dateToISOFormat(obj[arr[i]])).getTime();
	}
}

function emptyString2Null(obj) {
	for ( var i in obj) {
		if (obj[i] === "" || obj[i] === undefined) {
			delete obj[i];
		} else if (obj[i].constructor === (new Object()).constructor) {
			emptyString2Null(obj[i]);
		} else if (obj[i].constructor === (new Array()).constructor) {
			for ( var j in obj[i]) {
				emptyString2Null(obj[i][j]);
			}
		}
	}
}
function toEmptyString(obj){
	for ( var i in obj) {
		if (!obj[i] && obj[i] !== 0 && obj[i] !== '0') {
			obj[i] = '';
		}
	}
}

/**
 * 
 * @param {Object}
 *            obj
 * 
 * <pre>
 * obj = {
 *   String url,
 *   String type,
 *   Object data,
 *   Function callback,
 *   Ext.grid.Panel grid
 * }
 * </pre>
 */

function myRequest(obj) {
	if (!obj.url)
		return;
	obj.method = obj.method || obj.type || 'POST';
	emptyString2Null(obj.data);

	var requestData = {
		url : obj.url,
		method : obj.method,
		params : obj.params,
		jsonData : obj.jsonData,
		callback : function(options, success, response) {
			if (success) {
				var res = response.responseText;
				try {
					res = response.responseText.replace(/\n/g, '');
					res = Ext.decode(res);
				} catch (e) {}
				if (typeof obj.callback == 'function')
					obj.callback(res);
			} else {
				switch(response.status){
				case 403:
					logout();
					break;
				default:					
					Ext.Msg.alert('Error', response.statusText);
					break;
				}
			}
		}
	};

	Ext.Ajax.request(requestData);

}

function logout(){
	myRequest({
		url: 'rest/security/signout',
		callback:function(){
			window.location.href = 'login.html';			
		}
	});
}

function relayoutWindows() {
	clearTimeout(window.relayoutWindowsT);
	window.relayoutWindowsT = setTimeout(function() {
		Ext.WindowManager.each(function(win) {
			if (typeof win.doLayout === 'function'){
				win.doLayout();
				win.center();
			}
		});
	}, 100);
}

window.onresize = relayoutWindows;

function dateToISOFormat(str){
	if(!str) return '';
	return str.substr(6) + '-' + str.substr(3, 2) + '-' + str.substr(0, 2);
}

Date.prototype.ddmmyyyy = function() {
    var yyyy = this.getFullYear().toString();
    var mm = (this.getMonth()+1).toString(); // getMonth() is zero-based
    var dd  = this.getDate().toString();
    return (dd[1]?dd:"0"+dd[0]) + '-' + (mm[1]?mm:"0"+mm[0]) + '-' +yyyy ; // padding
};


function minutesToHours(t){
	t = parseInt(t);
	var hour = Math.floor(t / 60);
	var minute = Math.floor(t % 60);
	
	if(hour < 10) hour = '0' + hour;
	if(minute < 10) minute = '0' + minute;
	var time = hour + ':' + minute;
	return time;
};

function openPhoto(photo){
	var bg = document.createElement('div');
	bg.className = 'photo-background';
	
	var ph = document.createElement('img');
	ph.src = photo.src;
	ph.className = 'photo-fullscreen';
	
	document.body.appendChild(bg);
	document.body.appendChild(ph);
	
	bg.onclick = function(){
		closePhoto(bg, ph);
	}
	ph.onclick = function(){
		closePhoto(bg, ph);
	}
}

function closePhoto(bg, ph){
	bg.remove();
	ph.remove();
}