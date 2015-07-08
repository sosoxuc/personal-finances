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

        me.tbar = [ {
            xtype : 'label',
            html : '<p style="font-size:16px; margin:0;"><i>Personal</i> Finances</p>'
        }, '->', geokb, {
            text : 'გამოსვლა',
            handler : logout
        } ];

        var employees = Ext.create('TR.view.employees.MainPanel');

        var transactions = Ext.create('TR.view.transactions.MainPanel');

        var tabPanel = Ext.create('Ext.tab.Panel', {
            border : false,
            items : [ transactions, employees ]
        });

        me.items = [ tabPanel ];

        me.callParent(arguments);
    }
});