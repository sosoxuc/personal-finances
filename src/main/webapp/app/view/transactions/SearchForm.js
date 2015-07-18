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
        var directionsStore = Ext.StoreManager.lookup('directionsStore') || Ext.create('TR.store.directions.Store');
        
        var currenciesCombo = Ext.create('Ext.form.field.ComboBox', {
            name: 'currencyId',
            fieldLabel : 'ვალუტა',
            queryMode: 'local',
            store: currenciesStore,
            displayField: 'currencyCode',
            valueField: 'id'
        });
        
        var accountsCombo = Ext.create('Ext.form.field.ComboBox', {
            name: 'accountId',
            fieldLabel : 'ანგარიში',
            queryMode: 'local',
            store: accountsStore,
            displayField: 'accountName',
            valueField: 'id'
        });
        
        var projectsCombo = Ext.create('Ext.form.field.ComboBox', {
            name: 'projectId',
            fieldLabel : 'პროექტი',
            queryMode: 'local',
            store: projectsStore,
            displayField: 'projectName',
            valueField: 'id'
        });
        
        var directionCombo = Ext.create('Ext.form.field.ComboBox', {
            name: 'direction',
            fieldLabel : 'მიმართულება',
            queryMode: 'local',
            store: directionsStore,
            displayField: 'label',
            valueField: 'value'
        });
        
        me.defaults = {
            margin: '0 10 0 0'
        };
        
        var controls1 =  { 
                layout: 'vbox',
                xtype: 'fieldcontainer',
        }
        
        controls1.items = [{
            xtype: 'datefield',
            fieldLabel: 'დასაწყისი',
            name: 'startDate',
            format: 'd-m-Y'
        }, {
            xtype: 'datefield',
            fieldLabel: 'დასასრული',
            name: 'endDate',
            format: 'd-m-Y'
        } ];
        
        var controls2 =  { 
            layout: 'vbox',
            xtype: 'fieldcontainer',
            items: [currenciesCombo, accountsCombo]
        };
        
        var controls3 =  { 
            layout: 'vbox',
            xtype: 'fieldcontainer',
            items: [projectsCombo, directionCombo]
        };
        
        var filterButton =  {
            xtype: 'button',
            text: 'ფილტრი',
            handler: filter
        };
        
        var resetButton =  {
            xtype: 'button',
            text: 'გასუფთავება',
            handler: reset
        };
        
        me.items = [ controls1, controls2, controls3, filterButton, resetButton ];
        
        me.callParent(arguments);

        me.filter=filter;
        
        function filter(){
            var values = me.getForm().getValues(false,false,false,false);

            for (var property in values) {
                if (values.hasOwnProperty(property)) {
                    if (values[property] == '') {
                        delete values[property];
                    }
                }
            }
            me.searchedValues = values;
            cfg.grid.load(values);
        }
        
        function reset(){
            me.getForm().reset();
        }
        
    }
});