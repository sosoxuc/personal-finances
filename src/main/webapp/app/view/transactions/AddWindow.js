Ext.define("TR.view.transactions.AddWindow", {
	extend : "Ext.window.Window",
	modal : true,
	width : 400,
	height : 350,
	autoShow : true,
	layout : 'border',
	constructor : function(cfg) {
		cfg = cfg || {};
		var me = this;

		me.title = cfg.edit ? 'რედაქტირება' : 'დამატება';

		var form = Ext.create('Ext.form.Panel', {
			border : false,
			region : 'center',
			bodyPadding : 5,
			name : 'form',
			fieldDefaults : {
				labelWidth : 150,
				labelAlign : 'right',
				anchor : '100%',
				allowBlank : false
			},
			defaultType : 'textfield',
			items : [ {
				xtype : 'numberfield',
				hideTrigger : true,
				keyNavEnabled : false,
				mouseWheelEnabled : false,
				name : 'amount',
				fieldLabel : 'თანხა',
				allowBlank : false,
				format : '0.00'
			}]
		});

		me.items = [ form ];

		me.buttons = [{
			text : 'დამატება',
			handler : add
		}];

		me.callParent(arguments);

		form.getForm().findField('amount').focus();

		function add() {
			if (!form.getForm().isValid())
				return;
			var values = form.getForm().getValues();

//			correctDates(values, [ 'transactionDate' ]);

			myRequest({
				url : 'rest/transaction/create',
				params : values,
				callback : function(id) {
					cfg.searchForm.filter();
					me.close();
				}
			});
		}
	}
});