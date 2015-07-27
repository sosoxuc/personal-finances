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
        var projectsStore = Ext.StoreManager.lookup('projectsStore') || Ext.create('TR.store.projects.Store');
        var currenciesStore = Ext.StoreManager.lookup('currenciesStore') || Ext.create('TR.store.currencies.Store');
        var accountsStore = Ext.StoreManager.lookup('accountsStore') || Ext.create('TR.store.accounts.Store');
        var directionsStore = Ext.StoreManager.lookup('directionsStore') || Ext.create('TR.store.directions.Store');
        
        var currenciesCombo = Ext.create('Ext.form.field.ComboBox', {
            name: 'currencyId',
            fieldLabel : 'ვალუტა',
            queryMode: 'local',
            store: currenciesStore,
            displayField: 'currencyCode',
            valueField: 'id',
            value: cfg.data ? cfg.data.currencyId : ''
        });
        
        var accountsCombo = Ext.create('Ext.form.field.ComboBox', {
            name: 'accountId',
            fieldLabel : 'ანგარიში',
            queryMode: 'local',
            store: accountsStore,
            displayField: 'accountName',
            valueField: 'id',
            value: cfg.data ? cfg.data.accountId : ''
        });
        
        var projectsCombo = Ext.create('Ext.form.field.ComboBox', {
            name: 'projectId',
            queryMode: 'local',
            store: projectsStore,
            displayField: 'projectName',
            valueField: 'id',
            value: cfg.data ? cfg.data.projectId : ''
        });
        
        var projectsInput = {
            xtype: 'fieldcontainer',
            fieldLabel: 'პროექტი',
            layout: 'hbox',
            items: [ projectsCombo, {
                xtype: 'splitter'
            }, {
                xtype: 'button',
                text: '+',
                handler: addProject
            } ]
        }
        
        var directionCombo = Ext.create('Ext.form.field.ComboBox', {
            name: 'direction',
            fieldLabel : 'მიმართულება',
            queryMode: 'local',
            store: directionsStore,
            displayField: 'label',
            valueField: 'value',
            value: cfg.data ? Math.sign(cfg.data.transactionAmount) : ''
        });
        
        me.title = cfg.edit ? 'რედაქტირება' : 'დამატება';
        
        var now = new Date();

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
            items : [ directionCombo, {
                xtype : 'numberfield',
                hideTrigger : true,
                keyNavEnabled : false,
                mouseWheelEnabled : false,
                name : 'amount',
                fieldLabel : 'თანხა',
                allowBlank : false,
                format : '0.00',
                value: cfg.data ? cfg.data.transactionAmount : ''
            }, currenciesCombo, accountsCombo, projectsInput, {
                xtype: 'datefield',
                name: 'date',
                format: 'd-m-Y',
                fieldLabel : 'თარიღი',
                allowBlank : false,
                value: cfg.data ? cfg.data.transactionDate : now.ddmmyyyy()
            }, {
                name: 'note',
                fieldLabel : 'დანიშნულება',
                allowBlank : false,
                value: cfg.data ? cfg.data.transactionNote : ''
            }]
        });

        me.items = [ form ];

        me.buttons = [{
            text : 'დამატება',
            handler : cfg.edit ? edit : add
        }];

        
        me.callParent(arguments);
        me.on({
            show:function(){
                me.form.getForm().findField('amount').focus();
            }
        });

        function addProject(){
            Ext.create('TR.view.projects.AddWindow', {
                combo : projectsCombo
            });
        }
        
        function add() {
            if (!form.getForm().isValid())
                return;
            var values = form.getForm().getValues();

            myRequest({
                url : 'rest/transaction/create',
                params : values,
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

            myRequest({
                url : 'rest/transaction/update',
                params : values,
                callback : function(id) {
                    cfg.searchForm.filter();
                    me.close();
                }
            });
        }
    }
});