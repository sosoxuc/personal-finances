Ext.define("TR.view.profile.ChangePassword", {
    extend : "Ext.window.Window",
    modal : true,
    width : 500,
    autoHeight: true,
    constructor : function(cfg) {
        cfg = cfg || {};
        var me = this;
        
        var form = Ext.create('Ext.form.Panel', {
            border : false,
            bodyPadding : 5,
            name : 'form',
            fieldDefaults : {
                labelWidth : 150,
                labelAlign : 'right',
                anchor : '100%',
                allowBlank : false,
                inputType : 'password'
            },
            defaultType : 'textfield',
            items : [{
                name: 'oldPassword',
                fieldLabel : LANG.OLD_PASSWORD,
            },{
                name: 'newPassword',
                fieldLabel : LANG.NEW_PASSWORD,
            },{
                name: 'newPassword2',
                fieldLabel : LANG.NEW_PASSWORD2,
            }]
        });

        me.items = [ form ];
        
        me.buttons = [{
            text: LANG.CHANGE_PASSWORD
        }];
        
        me.callParent(arguments);
    }
});