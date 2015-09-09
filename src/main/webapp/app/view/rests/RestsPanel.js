Ext.define("TR.view.rests.RestsPanel", {
    extend : "Ext.panel.Panel",
    defaultType : 'textfield',
    title : LANG.CURRENT_RESTS,
    buttonAlign: 'left',
    constructor : function(cfg) {
        cfg = cfg || {};
        var me = this;       
                
        me.buttons = [{
            text : LANG.REFRESH,
            handler : cfg.load
        }];
        
        
        me.callParent(arguments);
        
        function loadData(data){
            me.removeAll();
            if (data) {
                Ext.Array.forEach(data, function(item){
                    me.add({
                        fieldLabel : item.resourceName,
                        labelAlign : 'right',
                        labelWidth : 150,
                        value: item.transactionRest,
                        readOnly: true
                    });
                });
            } else {
                me.add({
                    xtype : 'displayfield',
                    value: 'NO_DATA'
                });
            }
            me.unmask();
        }
        
        me.loadData = loadData;
    }
});