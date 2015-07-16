Ext.define("TR.view.transactions.SearchForm", {
    extend : "Ext.form.Panel",
    border: false,
    bodyPadding: 5,
    layout: 'hbox',
    fieldDefaults: {
        labelAlign: 'right'
    },
    constructor : function(cfg) {
        cfg = cfg || {};
        var me = this;
        
        cfg.grid.searchForm = me;
        
        var projectsStore = Ext.StoreManager.lookup('projectsStore') || Ext.create('TR.store.projects.Store');
        var currenciesStore = Ext.StoreManager.lookup('currenciesStore') || Ext.create('TR.store.currencies.Store');
        var accountsStore = Ext.StoreManager.lookup('accountsStore') || Ext.create('TR.store.accounts.Store');
        
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
            fieldLabel : 'პროექტი',
            queryMode: 'local',
            store: projectsStore,
            displayField: 'projectName',
            valueField: 'id',
            value: cfg.data ? cfg.data.projectId : ''
        });
        
        me.defaults = {
            margin: '0 10 0 0'
        };
        
        var controls =  { 
                layout: 'vbox',
                xtype: 'fieldcontainer',
        }
        
        controls.items = [{
            xtype: 'datefield',
            fieldLabel: 'დასაწყისი',
            name: 'startDate',
            format: 'd-m-Y',
            value: cfg.startDate ? new Date(cfg.startDate) : ''
        }, {
            xtype: 'datefield',
            fieldLabel: 'დასასრული',
            name: 'endDate',
            format: 'd-m-Y',
            value: cfg.endDate ? new Date(cfg.endDate) : ''
        }, currenciesCombo, accountsCombo, projectsCombo ];
        
        var button =  {
            xtype: 'button',
            text: 'ფილტრი',
            handler: filter
        };

        me.items = [ controls, button ];
        
        me.callParent(arguments);

        me.filter=filter;
        
        function filter(){
            var values = me.getForm().getValues();
            me.searchedValues = values;
            cfg.grid.load(values);
        }
    }
});