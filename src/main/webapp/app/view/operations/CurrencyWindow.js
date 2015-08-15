Ext.define("TR.view.operations.CurrencyWindow", {
    extend : "Ext.window.Window",
    modal : true,
    width : 500,
    autoHeight: true,
    constructor : function(cfg) {
        cfg = cfg || {};
        var me = this;
        
        me.title = LANG.ADD;
        
        var projectsStore = Ext.StoreManager.lookup('projectsStore') || Ext.create('TR.store.projects.Store');
        var currenciesStore = Ext.StoreManager.lookup('currenciesStore') || Ext.create('TR.store.currencies.Store');
        var accountsStore = Ext.StoreManager.lookup('accountsStore') || Ext.create('TR.store.accounts.Store');

        var currenciesCombo = Ext.create('Ext.form.field.ComboBox', {
            name: 'fromCurrencyId',
            queryMode: 'local',
            flex: 1,
            store: currenciesStore,
            displayField: 'currencyCode',
            valueField: 'id'
        });
        
        var amountField = {
            xtype : 'numberfield',
            hideTrigger : true,
            keyNavEnabled : true,
            mouseWheelEnabled : false,
            name : 'fromAmount',
            allowBlank : false,
            format : '0.00',
            flex: 1
        };
        
        var currenciesInput = {
            xtype: 'fieldcontainer',
            fieldLabel: LANG.CURRENCY,
            layout: 'hbox',
            items: [amountField, currenciesCombo, {
                xtype: 'button',
                text: '+',
                handler: addCurrency
            } ]
        };

        var currenciesCombo2 = Ext.create('Ext.form.field.ComboBox', {
            name: 'toCurrencyId',
            queryMode: 'local',
            flex: 1,
            store: currenciesStore,
            displayField: 'currencyCode',
            valueField: 'id'
        });
        
        var amountField2 = {
            xtype : 'numberfield',
            hideTrigger : true,
            keyNavEnabled : true,
            mouseWheelEnabled : false,
            name : 'toAmount',
            allowBlank : false,
            format : '0.00',
            flex: 1
        };
        
        var currenciesInput2 = {
            xtype: 'fieldcontainer',
            fieldLabel: LANG.CURRENCY,
            layout: 'hbox',
            items: [ amountField2, currenciesCombo2, {
                xtype: 'button',
                text: '+',
                handler: addCurrency
            } ]
        };

        
        var accountsCombo = Ext.create('Ext.form.field.ComboBox', {
            name: 'accountId',
            flex: 1,
            queryMode: 'local',
            store: accountsStore,
            displayField: 'accountName',
            valueField: 'id'
        });
        
        var accountsInput = {
            xtype: 'fieldcontainer',
            fieldLabel: LANG.ACCOUNT,
            layout: 'hbox',
            items: [ accountsCombo, {
                xtype: 'button',
                text: '+',
                handler: addAccount
            } ]
        };

        var projectsCombo = Ext.create('Ext.form.field.ComboBox', {
            name: 'projectId',
            queryMode: 'local',
            store: projectsStore,
            displayField: 'projectName',
            valueField: 'id',
            flex: 1
        });
        
        var projectsInput = {
            xtype: 'fieldcontainer',
            fieldLabel: LANG.PROJECT,
            layout: 'hbox',
            items: [ projectsCombo, {
                xtype: 'button',
                text: '+',
                handler: addProject
            } ]
        };
        
        
        
        var now = new Date();
        var dateField = {
            xtype: 'datefield',
            name: 'date',
            format: 'd-m-Y',
            fieldLabel : LANG.DATE,
            allowBlank : false,
            value: now.ddmmyyyy()
        };
        
        var noteField = {
            name: 'note',
            fieldLabel : LANG.DESTINATION,
            allowBlank : false
        };

        var form = Ext.create('Ext.form.Panel', {
            border : false,
            bodyPadding : 5,
            name : 'form',
            fieldDefaults : {
                labelWidth : 150,
                labelAlign : 'right',
                anchor : '100%',
                allowBlank : false
            },
            defaultType : 'textfield',
            items : [ 
                currenciesInput,
                currenciesInput2,
                accountsInput,
                projectsInput,
                dateField ,
                noteField
            ]
        });

        me.items = [ form ];

        me.buttons = [{
            text : LANG.ADD_AND_CLOSE,
            handler : addClose
        },{
            text : LANG.ADD,
            handler : add
        }];
        
        me.callParent(arguments);
        
        me.on({
            show: function() {
                form.getForm().findField('fromAmount').focus(true, 10);
            }
        });

        function addAccount(){
            Ext.create('TR.view.accounts.AddWindow', {
                combo : accountsCombo
            }).show();
        }
        
        function addCurrency(){
            Ext.create('TR.view.currencies.AddWindow', {
                combo : currenciesCombo
            }).show();
        }
        
        function addProject(){
            Ext.create('TR.view.projects.AddWindow', {
                combo : projectsCombo
            }).show();
        }
        
        function addClose() {
            if (!form.getForm().isValid())
                return;
            var values = form.getForm().getValues();

            myRequest({
                url : 'rest/operations/currency',
                params : values,
                callback : function() {
                    Ext.toast("Operation successed");
                    cfg.grid.store.load();
                    me.close();
                }
            });
        }
        
        function add() {
            if (!form.getForm().isValid())
                return;
            var values = form.getForm().getValues();

            myRequest({
                url : 'rest/operations/currency',
                params : values,
                callback : function() {
                    Ext.toast("Operation successed");
                    form.getForm().findField('fromAmount').setValue('');
                    form.getForm().findField('toAmount').setValue('');
                    form.getForm().findField('note').setValue('');
                    form.getForm().findField('fromAmount').focus(true, 10);
                    cfg.grid.store.load();
                }
            });
        }
    }
});