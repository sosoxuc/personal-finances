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
                name: 'oldPass',
                fieldLabel : LANG.OLD_PASSWORD,
            },{
                name: 'newPass',
                fieldLabel : LANG.NEW_PASSWORD,
            },{
                name: 'newPass2',
                fieldLabel : LANG.NEW_PASSWORD2,
            }]
        });

        me.items = [ form ];
        
        me.buttons = [{
            text: LANG.CHANGE_PASSWORD,
            handler: changePassword
        }];
        
        me.callParent(arguments);
        
        function changePassword(){
            if (!form.getForm().isValid())
                return;
            
            var values = form.getForm().getValues();
            
            myRequest({
                url : 'rest/security/password/change',
                params : values,
                callback : function() {
                    me.close();
                    Ext.toast("Password change successful");
                }
            });
        }
    }
});