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

        me.tbar = [{
            xtype : 'image',
            src : './images/icon.png',
            height : 32,
            width : 32
        }, {
            xtype : 'label',
            html : '<p style="font-size:16px; margin:0;color:#3892d4;font-weight:bold;"><i style="color:#000000;font-weight:normal;">Personal</i> FINANCES</p>'
        }, '->', geokb, {
            text : LANG.LOGOUT,
            handler : logout
        } ];

        var employees = Ext.create('TR.view.employees.MainPanel');
        var transactions = Ext.create('TR.view.transactions.MainPanel');
        var projects = Ext.create('TR.view.projects.MainPanel');
        var rests = Ext.create('TR.view.rests.MainPanel');
        var currencies = Ext.create('TR.view.currencies.MainPanel');
        var accounts = Ext.create('TR.view.accounts.MainPanel');
        var imports = Ext.create('TR.view.imports.MainPanel');
        var tabPanel = Ext.create('Ext.tab.Panel', {
            border : false,
            items : [ rests, 
                      transactions, 
                      //employees, 
                      projects, 
                      accounts, 
                      currencies, 
                      imports ]
        });

        me.items = [ tabPanel ];

        me.callParent(arguments);
    }
});
