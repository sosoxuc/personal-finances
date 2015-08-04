Ext.define("TR.view.transactions.AddWindow", {
    extend : "Ext.window.Window",
    modal : true,
    width : 500,
    autoHeight: true,
    constructor : function(cfg) {
        cfg = cfg || {};
        var me = this;
        
        me.title = cfg.edit ? LANG.EDIT : LANG.ADD;
        
        var projectsStore = Ext.StoreManager.lookup('projectsStore') || Ext.create('TR.store.projects.Store');
        var currenciesStore = Ext.StoreManager.lookup('currenciesStore') || Ext.create('TR.store.currencies.Store');
        var accountsStore = Ext.StoreManager.lookup('accountsStore') || Ext.create('TR.store.accounts.Store');
        var directionsStore = Ext.StoreManager.lookup('directionsStore') || Ext.create('TR.store.directions.Store');
        
        var directionCombo = Ext.create('Ext.form.field.ComboBox', {
            name: 'direction',
            fieldLabel : LANG.DIRECTION,
            queryMode: 'local',
            store: directionsStore,
            displayField: 'label',
            valueField: 'value',
            value: cfg.data ? Math.sign(cfg.data.transactionAmount) : ''
        });

        var currenciesCombo = Ext.create('Ext.form.field.ComboBox', {
            name: 'currencyId',
            queryMode: 'local',
            flex: 1,
            store: currenciesStore,
            displayField: 'currencyCode',
            valueField: 'id',
            value: cfg.data ? cfg.data.currencyId : ''
        });
        
        var currenciesInput = {
            xtype: 'fieldcontainer',
            fieldLabel: LANG.CURRENCY,
            layout: 'hbox',
            items: [ currenciesCombo, {
                xtype: 'button',
                text: '+',
                handler: addProject
            } ]
        };


        var accountsCombo = Ext.create('Ext.form.field.ComboBox', {
            name: 'accountId',
            flex: 1,
            queryMode: 'local',
            store: accountsStore,
            displayField: 'accountName',
            valueField: 'id',
            value: cfg.data ? cfg.data.accountId : ''
        });
        
        var accountsInput = {
            xtype: 'fieldcontainer',
            fieldLabel: LANG.ACCOUNT,
            layout: 'hbox',
            items: [ accountsCombo, {
                xtype: 'button',
                text: '+',
                handler: addProject
            } ]
        };

        var projectsCombo = Ext.create('Ext.form.field.ComboBox', {
            name: 'projectId',
            queryMode: 'local',
            store: projectsStore,
            displayField: 'projectName',
            valueField: 'id',
            flex: 1,
            value: cfg.data ? cfg.data.projectId : ''
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
        
        var amountField = {
            xtype : 'numberfield',
            hideTrigger : true,
            keyNavEnabled : true,
            mouseWheelEnabled : false,
            name : 'amount',
            fieldLabel : LANG.SUM,
            allowBlank : false,
            format : '0.00',
            value: cfg.data ? cfg.data.direction * cfg.data.transactionAmount : ''
        };
        
        var now = new Date();
        var dateField = {
            xtype: 'datefield',
            name: 'date',
            format: 'd-m-Y',
            fieldLabel : LANG.DATE,
            allowBlank : false,
            value: cfg.data ? cfg.data.transactionDate : now.ddmmyyyy()
        };
        
        var noteField = {
            name: 'note',
            fieldLabel : LANG.DESTINATION,
            allowBlank : false,
            value: cfg.data ? cfg.data.transactionNote : ''
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
                directionCombo, 
                amountField, 
                currenciesInput, 
                accountsInput, 
                projectsInput, 
                dateField , 
                noteField
            ]
        });

        me.items = [ form ];

        me.buttons = [{
            text : cfg.edit ? LANG.EDIT : LANG.ADD,
            handler : cfg.edit ? edit : add
        }];

        me.callParent(arguments);
        
        me.on({
            show: function() {
                form.getForm().findField('amount').focus(true, 10);
            }
        });

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
                url : 'rest/transaction/create',
                params : values,
                callback : function() {
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
                url : 'rest/transaction/create',
                params : values,
                callback : function() {
                    cfg.grid.store.load();
                }
            });
        }
        
        function edit() {
            if (!form.getForm().isValid())
                return;
            var values = form.getForm().getValues();
            values.transactionId = cfg.data.id;
            values.version = cfg.data.version;
            
            myRequest({
                url : 'rest/transaction/update',
                params : values,
                callback : function() {
                    cfg.grid.store.load();
                    me.close();
                }
            });
        }
    }
});