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

        var directionsStore = Ext.create('Ext.data.Store', {
            fields: [
                {name: 'label'},
                {name: 'value', type: 'int'}
            ], 
            data: [
                {label: 'გასავალი', value: -1},
                {label: 'შემოსავალი', value: 1}
            ]
        });
        
        var projectsCombo = Ext.create('Ext.form.field.ComboBox', {
            name: 'projectId',
            emptyText: 'პროექტი',
            fieldLabel : 'პროექტი',
            queryMode: 'local',
            store: projectsStore,
            displayField: 'projectName',
            valueField: 'id',
            value: cfg.data ? cfg.data.projectId : ''
        });
        
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
                format : '0.00',
                value: cfg.data ? cfg.data.transactionAmount : ''
            }, projectsCombo, {
                xtype: 'datefield',
                name: 'date',
                format: 'd-m-Y',
                fieldLabel : 'თარიღი',
                allowBlank : false,
                disabled: cfg.edit,
                value: cfg.data ? cfg.data.transactionDate : ''
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