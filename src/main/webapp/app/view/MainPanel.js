Ext.define('TR.view.MainPanel', {
	extend : 'Ext.panel.Panel',
	border : false,
	layout : 'fit',
	constructor : function(cfg) {
		cfg = cfg || {};
		var me = this;

		window.geokb = Ext.create('Ext.button.Button', {
			text : 'KA',
			tooltip : 'ქართული კლავიატურა',
			enableToggle : true,
			pressed : true,
			toggleHandler : function(item, pressed) {
				if (pressed) {
					this.setText('KA');
				} else {
					this.setText('EN');
				}
			}
		});
		var geokbField = {
			xtype : 'textfield',
			hidden : true
		};
		changeVal(geokbField, geokb);

		me.tbar = [ {
			xtype : 'label',
			html : '<b>Personal Finances System</b>'
		}, '->', geokb, {
			text : 'გამოსვლა',
			handler : logout
		} ];

		var employeePanel = null;
		var statsPanel = null;
		if (cfg.employee.userRole == 2) {
			employeePanel = Ext.create('TR.view.employees.MainPanel', {
				title : 'თანამშრომლები'
			});

//			statsPanel = Ext.create('TR.view.stats.MainPanel', {
//				title : 'სტატისტიკა'
//			});
		}

		var userTab = Ext.create('TR.view.transactions.MainPanel', {
			employeeId : cfg.employee.id,
			title : 'ტრანზაქციები',
			closable : false
		});
		
		var tabPanel = Ext.create('Ext.tab.Panel', {
			border : false,
			items : [userTab, employeePanel, statsPanel]
		});

		me.items = [ tabPanel ];

		tr.tabPanel = tabPanel;

		me.callParent(arguments);
	}
});