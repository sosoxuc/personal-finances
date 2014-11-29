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
				name : 'id',
				hidden : true,
				allowBlank : true
			}, {
				xtype : 'datefield',
				fieldLabel : 'თარიღი',
				allowBlank : false,
				name : 'transactionDate',
				emptyText : 'dd/mm/yyyy',
				format : 'd/m/Y'
			}, {
				xtype : 'numberfield',
				hideTrigger : true,
				keyNavEnabled : false,
				mouseWheelEnabled : false,
				name : 'transactionAmount',
				fieldLabel : 'თანხა',
				allowBlank : false,
				format : '0.00'
			}, {
				fieldLabel : 'პროექტი',
				name : 'projectName',
				allowBlank : true
			}, {
				fieldLabel : 'დანიშნულება',
				name : 'reasonName',
				allowBlank : true
			}, {
				fieldLabel : 'დასახელება',
				name : 'debitAccountName',
				allowBlank : true
			}, {
				fieldLabel : 'კორესპოდენტი',
				name : 'creditAccountName',
				allowBlank : true
			}, {
				fieldLabel : 'შენიშვნა',
				name : 'note',
				allowBlank : true,
				xtype : 'textarea'
			} ]
		});
		log(form);
		me.items = [ form ];

		if (!cfg.edit) {
			var check = ({
				xtype : 'checkbox',
				boxLabel : 'არ დაიხუროს ფანჯარა'
			});

			me.buttons = [ check, '->' ];
		} else {
			me.buttons = [];
		}

		me.buttons.push({
			text : cfg.edit ? 'რედაქტირება' : 'დამატება',
			handler : cfg.edit ? edit : add
		});

		me.callParent(arguments);

		form.getForm().findField('transactionDate').focus();

		function add() {
			if (!form.getForm().isValid())
				return;
			var values = form.getForm().getValues();

			delete values.id;

			correctDates(values, [ 'transactionDate' ]);

			log(values);
			myRequest({
				url : 'rest/transaction/create',
				jsonData : values,
				callback : function(id) {
					cfg.searchForm.filter();
					me.close();
				}
			});
		}

		function edit() {
			if (!form.getForm().isValid())
				return;
			var values = form.getForm().getValues();
			correctDates(values, [ 'transactionDate' ]);

			myRequest({
				url : 'rest/transaction/update',
				params : values,
				callback : function(id) {
					cfg.searchForm.filter();
					me.close();
				}
			});
		}

		function addPosition() {
			Ext.Msg.prompt("პოზიციის დამატება", "პოზიცია", function(ans, text) {
				if (ans == 'ok' && text) {
					myRequest({
						url : 'rest/employee/addPosition',
						params : {
							name : text
						},
						callback : function(id) {
							if (id)
								positionStore.load();
							positionCombo.setValue(id);
						}
					});
				}
			});
		}

		function addWorkplace() {
			Ext.Msg.prompt("სამუშაო ადგილის დამატება", "სამუშაო ადგილი",
					function(ans, text) {
						if (ans == 'ok' && text) {
							myRequest({
								url : 'rest/employee/addWorkplace',
								params : {
									name : text
								},
								callback : function(id) {
									if (id)
										workplaceStore.load();
									workplaceCombo.setValue(id);
								}
							});
						}
					});
		}
	}
});