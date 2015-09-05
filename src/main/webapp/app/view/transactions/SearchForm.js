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
        var employeesStore = Ext.StoreManager.lookup('employeeStore') || Ext.create('TR.store.employees.EmployeeStore');
        
        var employeesCombo = Ext.create('Ext.form.field.ComboBox', {
            name: 'employeeId',
            fieldLabel : LANG.EMPLOYEE,
            queryMode: 'local',
            store: employeesStore,
            displayField: 'fullName',
            valueField: 'id',
            multiSelect: true
        });
        
        var ordinaryCheck = Ext.create('Ext.form.field.Checkbox', {
            xtype: 'checkbox',
            name: 'ordinary',
            submitValue: true,
            fieldLabel : LANG.REAL
        });
        
        var plannedCheck = Ext.create('Ext.form.field.Checkbox', {
            xtype: 'checkbox',
            name: 'planned',
            submitValue: true,
            fieldLabel : LANG.PLANNED
        });
        
        var currenciesCombo = Ext.create('Ext.form.field.ComboBox', {
            name: 'currencyId',
            fieldLabel : LANG.CURRENCY,
            queryMode: 'local',
            store: currenciesStore,
            displayField: 'currencyCode',
            valueField: 'id',
            multiSelect: true
        });
        
        var accountsCombo = Ext.create('Ext.form.field.ComboBox', {
            name: 'accountId',
            fieldLabel : LANG.ACCOUNT,
            queryMode: 'local',
            store: accountsStore,
            displayField: 'accountName',
            valueField: 'id',
            multiSelect: true,
        });
        
        var projectsCombo = Ext.create('Ext.form.field.ComboBox', {
            name: 'projectId',
            fieldLabel : LANG.PROJECT,
            queryMode: 'local',
            store: projectsStore,
            displayField: 'projectName',
            valueField: 'id',
            multiSelect: true
        });
        
        var directionCombo = Ext.create('Ext.form.field.ComboBox', {
            name: 'direction',
            fieldLabel : LANG.DIRECTION,
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
            fieldLabel: LANG.DATE_FROM,
            name: 'startDate',
            format: 'd-m-Y'
        }, {
            xtype: 'datefield',
            fieldLabel: LANG.DATE_TO,
            name: 'endDate',
            format: 'd-m-Y'
        }, {
            xtype: 'textfield',
            fieldLabel: LANG.DESTINATION,
            name: 'note'
        }, employeesCombo];
        
        var controls2 =  { 
            layout: 'vbox',
            xtype: 'fieldcontainer',
            items: [currenciesCombo, accountsCombo, ordinaryCheck, plannedCheck]
        };
        
        var controls3 =  { 
            layout: 'vbox',
            xtype: 'fieldcontainer',
            items: [projectsCombo, directionCombo]
        };
        
        
        var filterButton =  {
            xtype: 'button',
            text: LANG.FILTER,
            handler: filter
        };
        
        var resetButton =  {
            xtype: 'button',
            text: LANG.CLEAR,
            handler: reset
        };
        
        me.items = [ controls1, controls2, controls3, filterButton, resetButton ];
        
        me.callParent(arguments);

        me.filter=filter;
        
        function filter(){
            var values = me.getForm().getValues(false,false,false,false);
            me.down
            for (var property in values) {
                if (values.hasOwnProperty(property)) {
                    if (values[property] == '') {
                        delete values[property];
                    }
                }
            }
            me.searchedValues = values;
            cfg.grid.load(values);
            cfg.grid.exporthref(values);
        }
        
        function reset(){
            me.getForm().reset();
        }
        
    }
});
