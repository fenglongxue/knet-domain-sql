var $ = layui.jquery;
var form = layui.form;
var pageNumber = 1;
var editor;
var pageNumberRight = 1;
window.onload = function () {
    editor = CodeMirror.fromTextArea(document.getElementById('code'), {
        mode: "text/x-plsql",
        indentWithTabs: true,
        smartIndent: true,
        lineNumbers: true,
        matchBrackets: true,
        autofocus: true,
    });
    editor.setSize("", "calc( 100vh - 50px )");
    // editor.on("keyup", function (cm) {
    //     CodeMirror.showHint(cm, CodeMirror.hint.deluge, {completeSingle: false});
    // });
};

const sqlexc = commonCtx+'/sql/exc',
    logshare = commonCtx+'/log/share',
    loglist = commonCtx+'/log/list',
    logImp = commonCtx+'/log/imp',
    loglistDelete = commonCtx+'/log/delete';

const updateExc = commonCtx+'/sql/updateExc'; //执行
const updateListForSelect = commonCtx+'/sql/updateForPl'; //批量更新

var sqlInit = {
    tabTitle:[], //头部切换划过显示内容
    sqlTypeInput:'',    //头部产品类型选择值
    check: function (){
        var sqlType = $('#sqlType').val();
        var sqlCode = editor.getValue();
        var sqlArrk = sqlCode.split(';');

        //去除空元素
        var sqlArr = sqlArrk.filter(function (s) {
            return s && s.trim();
        });
        layer.load(2);
        sqlInit.sqlTypeInput = sqlType;
        sqlInit.tabTitle= [];

        pageNumber = 1;
        $('#result').html('');

        $.post( sqlexc, {sqls:sqlArr, type: sqlType, pageNumber: pageNumber }, function (data) {
            layer.closeAll();
            $.each(data,function (i,v){
                let sqltypes = v.sqlType;
                if( sqltypes == 'SELECT' ){
                    //查询
                    sqlInit.getselectData(v,i);
                } else if( sqltypes == 'UPDATESELECT' ){
                    // 更新
                    sqlInit.getupdateData(v,i);
                } else {
                    sqlInit.getOtherUpdate(v,i);
                }
            })
        })
    },
    batchUpdate: function (){
        var sqlType = $('#sqlType').val();
        var sqlCode = editor.getValue();
        var sqlArrk = sqlCode.split(';');

        //去除空元素
        var sqlArr = sqlArrk.filter(function (s) {
            return s && s.trim();
        });

        layer.load(2);
        sqlInit.sqlTypeInput = sqlType;
        sqlInit.tabTitle= [];

        $.post( updateListForSelect, {sqls:sqlArr, type: sqlType }, function (data) {
            layer.closeAll();
            sqlInit.getbatchUpdate(data);
        })
    },
    getTitle: function (tabTitle){
        let html = '';
        $.each(tabTitle,function (i,v){
            let cls = i == 0 ? 'layui-this' : '';
            let type = v.title;
            if( type == 'SELECT' ){
                html +="<li class='title"+i+" "+cls+"' onmouseenter=sqlInit.alertTips("+i+",'title"+i+"') onmouseleave=sqlInit.closeLayer()>查询</li>";
            } else if( type == 'UPDATESELECT' ){
                html +="<li class='title"+i+" "+cls+"' onmouseenter=sqlInit.alertTips("+i+",'title"+i+"') onmouseleave=sqlInit.closeLayer()>更新</li>";
            } else if ( type == 'batchUpdate' ){
                html +="<li class='title"+i+" "+cls+"' >批量更新</li>";
            } else if( type == 'ALTER' || type == 'CREATETABLE' || type == 'DROP' || type == 'COMMENT' ){
                html +="<li class='title"+i+" "+cls+"' onmouseenter=sqlInit.alertTips("+i+",'title"+i+"') onmouseleave=sqlInit.closeLayer()>表操作</li>";
            } else if ( type == 'INSERT' ){
                html +="<li class='title"+i+" "+cls+"' onmouseenter=sqlInit.alertTips("+i+",'title"+i+"') onmouseleave=sqlInit.closeLayer()>插入</li>";
            } else if ( type == 'NONE' ){
                html +="<li class='title"+i+" "+cls+"' onmouseenter=sqlInit.alertTips("+i+",'title"+i+"') onmouseleave=sqlInit.closeLayer()>结果</li>";
            }
        })
        $('#tabTitle').html( html );
    },
    alertTips: function (vindex,title){
        layer.tips(sqlInit.tabTitle[vindex].sql, '.'+title, {
            tips: 1,
            area: ['450px', 'auto'],
            time:100000
        });
    },
    closeLayer: function (){
        layer.closeAll();
    },
    getselectData: function (dvalue,dindex) {
        sqlInit.tabTitle.push( { sql:dvalue.sql, title: dvalue.sqlType } );
        var thtml = '';

        //表格
        var cls = dindex == 0? 'layui-show' : '';
        thtml += '<div class="layui-tab-item '+cls+'">';

        thtml+='<div class="sql-title">';
        thtml+='<span class="sql-tip">'+dvalue.msg+'</span>';

        if( dvalue.code == 1000 ) {
            thtml += '     <div class="layui-btn-group">';
            thtml += '         <button type="button" class="layui-btn layui-btn-primary layui-btn-xs" id="moreSelect" data-id="table' + (dindex + 1) + '" data-sql="' + dvalue.sql + '"><i class="layui-icon"> &#xe625;</i>更多</button>';
            thtml += '         <button type="button" class="layui-btn layui-btn-primary layui-btn-xs" id="refreshSelect" data-id="table' + (dindex + 1) + '" data-sql="' + dvalue.sql + '"><i class="layui-icon"> &#xe669;</i>刷新</button>';
            thtml += '         <button type="button" class="layui-btn layui-btn-primary layui-btn-xs" onclick=sqlInit.logCollection(' + dindex + ',"SAVE")><i class="layui-icon"> &#xe658;</i>收藏sql</button>';
            thtml += '         <button type="button" class="layui-btn layui-btn-primary layui-btn-xs" onclick=sqlInit.logCollection(' + dindex + ',"SHARE")><i class="layui-icon"> &#xe641;</i>分享sql</button>';
            thtml += '         <button type="button" class="layui-btn layui-btn-primary layui-btn-xs" onclick=sqlInit.logExport(' + dindex + ',' + dvalue.count + ')><i class="layui-icon"> &#xe67d;</i>导出</button>';
            thtml += '     </div>';
        }

        thtml+=' </div>';

        //取出键值
        var thisTitle = [];

        thtml += '<div class="layui-table-frame select-table-frame"><table class="layui-table" id="table'+(dindex+1)+'" lay-size="sm">';
        //循环表头
        thtml += '<tr>';
        $.each(dvalue.title,function (i,v){
            thisTitle.push( v );
            thtml +='<th>'+v+'</th>'
        })
        thtml+='</tr>';

        //循环表格值
        $.each(dvalue.data,function (rindex,rvalue){
            thtml += '<tr>';
            $.each(rvalue,function (tindex,tvalue){
                $.each(thisTitle,function (i,v){
                    if( tindex == v ){
                        thtml +='<td><div class="layui-table-cell">'+tvalue+'</div></td>'
                    }
                })
            })
            thtml+='</tr>';
        })
        thtml += '</table></div>';
        thtml += '</div>';

        $('#result').append(thtml);

        //导航
        sqlInit.getTitle(sqlInit.tabTitle);

        $('#moreSelect').on('click',function (){
            sqlInit.moreSelect($(this),'more');
        })
        $('#refreshSelect').on('click',function (){
            sqlInit.moreSelect($(this),'refresh');
        })
    },
    getupdateData: function (dvalue,dindex){
        sqlInit.tabTitle.push( { sql:dvalue.sql, title: dvalue.sqlType } );
        // var type = data[0].sqlType;
        var thtml = '';
        //表格
        // $.each(data,function (dindex,dvalue){
            var cls = dindex == 0? 'layui-show' : '';
            thtml += '<div class="layui-tab-item '+cls+'">';
            thtml+='<div class="sql-title">';
            thtml+='<span class="sql-tip">'+dvalue.msg+'</span>';

            if( dvalue.code == 1000 ){
                thtml+='<div class="layui-btn-group">';
                thtml+='    <button type="button" class="layui-btn layui-btn-primary layui-btn-xs" onclick=sqlInit.logUpdateExc('+dindex+',this)><i class="layui-icon"> &#xe605;</i>执行</button>';
                thtml+='         <button type="button" class="layui-btn layui-btn-primary layui-btn-xs" onclick=sqlInit.logCollection('+dindex+',"SAVE")><i class="layui-icon"> &#xe658;</i>收藏sql</button>';
                thtml+='         <button type="button" class="layui-btn layui-btn-primary layui-btn-xs" onclick=sqlInit.logCollection('+dindex+',"SHARE")><i class="layui-icon"> &#xe641;</i>分享sql</button>';
                thtml+='</div>';
            }
            thtml+=' </div>';

            thtml += '<table class="layui-table" lay-size="sm">';
            //循环表头
            thtml += '<tr>';
            $.each(dvalue.title,function (i,v){
                thtml +='<th>'+v+'</th>'
            })
            thtml+='</tr>';

            //循环表格值
            $.each(dvalue.map,function (rindex,rvalue){
                thtml += '<tr>';
                $.each(rvalue,function (tindex,tvalue){
                    $.each(dvalue.title,function (i,v){
                        if( tvalue.key.toUpperCase() == v ){
                            thtml +='<td>'+tvalue.oldValue+''

                            if( tvalue.update ){
                                thtml +='--><span style="color: green">'+tvalue.newValue+'</span>'
                            }
                            thtml += '</td>';
                        }
                    })
                })
                thtml+='</tr>';
            })
            thtml += '</table>';
            thtml += '</div>';
        // })

        $('#result').append(thtml);

        //导航
        sqlInit.getTitle(sqlInit.tabTitle);
    },
    //其他类型
    getOtherUpdate: function (dvalue,dindex){
        var thtml = '';
        sqlInit.tabTitle.push( { sql:dvalue.sql, title: dvalue.sqlType } );
        //表格
        var cls = dindex == 0? 'layui-show' : '';
        thtml += '<div class="layui-tab-item '+cls+'">';

        thtml+='<div class="sql-title">';
        thtml+='<span class="sql-tip">'+dvalue.msg+'</span>';
        if( dvalue.code == 1000 ){
            thtml+='<div class="layui-btn-group">';
            thtml+='    <button type="button" class="layui-btn layui-btn-primary layui-btn-xs" onclick=sqlInit.logUpdateExc('+dindex+',this)><i class="layui-icon"> &#xe605;</i>执行</button>';
            thtml+='         <button type="button" class="layui-btn layui-btn-primary layui-btn-xs" onclick=sqlInit.logCollection('+dindex+',"SAVE")><i class="layui-icon"> &#xe658;</i>收藏sql</button>';
            thtml+='         <button type="button" class="layui-btn layui-btn-primary layui-btn-xs" onclick=sqlInit.logCollection('+dindex+',"SHARE")><i class="layui-icon"> &#xe641;</i>分享sql</button>';
            thtml+='</div>';
        }
        thtml+=' </div>';

        // thtml += '<div class = "sql-p10">'+dvalue.msg+'</div>';
        thtml += '</div>';
        $('#result').append(thtml);

        //导航
        sqlInit.getTitle(sqlInit.tabTitle);
    },
    //批量更新
    getbatchUpdate: function (data){
        var thtml = '';
        sqlInit.tabTitle.push( { sql:'', title: 'batchUpdate' } );
        //表格
        thtml += '<div class="layui-tab-item layui-show"><div class="sql-p10">';

        $.each(data,function (i,v){
            thtml += '<p>'+v.msg+'<span>'+v.sql+'</span></p>';
        })

        thtml += '</div></div>';
        $('#result').html(thtml);

        //导航
        sqlInit.getTitle(sqlInit.tabTitle);
    },
    //底部收起
    domShow:function () {
        var tab = $('#resContent');
        if( tab.hasClass('closeTab') ){
            tab.removeClass('closeTab').css('height','300px');
            $('#closeBtn i').html('&#x1006;');
        } else {
            tab.addClass('closeTab').css('height','30px');
            $('#closeBtn i').html('&#xe65a;');
        }
    },
    logCollection: function ( tindex , storType ){
        layer.prompt({title: '输入标题'}, function(pass, index){
            layer.close(index);

            $.post( logshare, {sql: sqlInit.tabTitle[tindex].sql, type: sqlInit.sqlTypeInput, storType: storType,title: pass }, function (data) {
                if( data.code == 1000 ){
                    let tips = storType == 'SHARE' ? '分享成功' : '收藏成功';
                    layer.msg(tips);
                    pageNumberRight = 1;
                    sqlInit.getlogList('','');
                }
            })
        });
    },
    // 导出
    logExport: function ( tindex , num ){
        if( num > 10000 ){
            layer.msg('大于10000条数据不能导出！');
            return;
        }
        if( num == 0 ){
            layer.msg('0条数据不能导出！');
            return;
        }
        var html = '<div class="layui-form">';
        html+='<input id="sigo" type="text" class="layui-layer-input" value="" placeholder="请输入钉钉签报号" lay-verify="required" autocomplete="off">';
        html+='<input id="email" style="margin-top:10px;" type="text" class="layui-layer-input" value="" placeholder="请输入邮箱" lay-verify="email" autocomplete="off">';
        html+='<input id="impTitle" style="margin-top:10px;" type="text" class="layui-layer-input" value="" placeholder="请输入标题" lay-verify="required" autocomplete="off">';
        html+='<button type="submit" id="logExportBtn" class="layui-btn layui-btn-sm layui-btn-normal export-btn" lay-submit="" lay-filter="logExportBtn">确定</button>';
        html+='</div>';

        layer.open({
            type: 1 //Page层类型
            ,btn:[]
            ,title: '导出'
            ,skin: 'layui-layer-prompt layui-layer-export'
            ,content: html
        });
        //确认导出
        form.on('submit(logExportBtn)', function(data){
            var thisData = {
                sql: sqlInit.tabTitle[tindex].sql,
                type: sqlInit.sqlTypeInput,
                sigo: $('#sigo').val(),
                title: $('#impTitle').val(),
                email: $('#email').val(),
            };
            layer.closeAll();
            layer.load(2);
            $.post( logImp, thisData , function (data) {
                layer.closeAll();
                if( data.code == 1000 ){
                    layer.alert(data.msg);
                }
            })
            return false;
        });
    },
    //执行
    logUpdateExc: function ( tindex , that){
        var sqls = [sqlInit.tabTitle[tindex].sql];
        $.post( updateExc, {sqls: sqls, type: sqlInit.sqlTypeInput}, function (data) {
            if( data[0].code == 1000 ){
                $(that).parents('.sql-title').find('.sql-tip').html(data[0].msg);
            } else {
                $(that).parents('.sql-title').find('.sql-tip').html('执行失败！')
            }
        })

        $(that).parents('.layui-tab-item').find('.layui-table').remove();
        $(that).remove();
    },
    //更多
    moreSelect:function (that,type){
        if( type == 'more' ){
            pageNumber++;
        } else {
            pageNumber=1;
        }
        $.post( sqlexc, {sqls:[that.attr('data-sql')], type: sqlInit.sqlTypeInput, pageNumber: pageNumber }, function (data) {
            if( data[0].code == 1002 ){
                layer.msg(data[0].msg);
            } else {
                var thtml = '';
                $.each(data[0].data,function (dindex,dvalue){
                    thtml += '<tr>';
                    $.each(dvalue,function (tindex,tvalue){
                        $.each(data[0].title,function (i,v){
                            if( tindex == v ){
                                thtml +='<td><div class="layui-table-cell">'+tvalue+'</div></td>'
                            }
                        })
                    })
                    thtml+='</tr>';
                })
                var thisid = that.attr('data-id');

                if( type == 'more' ){
                    $('#'+thisid).append(thtml);
                } else {
                    $('#'+thisid).html(thtml);
                }
            }

        })
    },
    //右侧列表
    getlogList: function (storType,storTit,ismore){
        // storType: 分享/收藏
        // storTit：关键字
        // ismore： 是否是更多
        // type ： wz/seal
        $.post( loglist, {pageNumber: pageNumberRight,storType:storType ,title:storTit,type: $('#sqlType').val()}, function (data) {
            if( data.code == 1000 ){
                var html = '';
                var res = data.data;
                $.each(res,function (i,v){
                    html +='<tr><td id="editTb'+i+'" data-index="'+i+'"><span class="edit" data-index="'+i+'">'+v.TITLE+'</span>';
                    // 类型为分享（SHARE）时，显示名字
                    if( v.STOR_TYPE.toUpperCase() == 'SHARE' ){
                        html+='<button class="layui-btn layui-btn-xs layui-bg-red right-user">'+v.NAME+'</button>'
                    }
                    html+='<div class="deleteList" onclick=sqlInit.deleteList("'+v.ID+'")><i class="layui-icon layui-icon-delete"></i></div></td></tr>';
                })
                if( ismore ){
                    $('#sqlRightList').append(html);
                } else {
                    $('#sqlRightList').html(html);
                }

                $('#sqlRightList tr td').hover(function (){
                    let tindex = $(this).attr('data-index');
                    layer.tips(res[tindex].SQL, '#editTb'+tindex, {
                        tips: 4,
                        area: ['500px', 'auto'],
                        time:100000
                    });
                    $(this).find('.handle').addClass('light');
                },function (){
                    layer.closeAll();
                    $(this).find('.handle').removeClass('light');
                })
                data.data.length < 50 ? $('#morelist').hide() : $('#morelist').show();

                $('.edit').on('click',function (){
                    let tindex = $(this).attr('data-index');
                    let vals = res[tindex].SQL;
                    editor.getDoc().setValue(vals);
                })

            }
        })
    },
    //右侧列表删除
    deleteList: function (tid) {
        layer.confirm('确认删除？', {
            btn: ['确认','取消'] //按钮
        }, function(){
            $.post( loglistDelete, {id:tid}, function (data) {
                if( data.code == 1000 ){
                    layer.msg(data.msg);
                    sqlInit.getlogList('','');
                }
            })
        });
    },
}

$(function(){
    //运行
    $('#check').on('click',sqlInit.check);
    $('#batchUpdate').on('click',sqlInit.batchUpdate);

    //底部关闭按钮
    $('#closeBtn').on('click',sqlInit.domShow);

    //右侧列表
    sqlInit.getlogList('','');

    $('#storType').change(function (){
        var val = $(this).val();
        var inpval = $('#storTypeInp').val();
        pageNumberRight = 1;
        sqlInit.getlogList(val,inpval);
    })

    $('#storTypeInp').keydown(function () {
        if (event.keyCode == "13") {
            var selval = $('#storType').val();
            var inpval = $(this).val();
            pageNumberRight = 1;
            sqlInit.getlogList(selval,inpval);
        }
    });
    var drag = new Drag();

    //右侧列表更多
    $('#morelist').on('click',function (){
        //右侧列表
        pageNumberRight ++;
        sqlInit.getlogList('','',true);
    })

    $('#sqlType').on('change',function (){
        var selval = $('#storType').val();
        var inpval = $('#storTypeInp').val();
        pageNumberRight = 1;
        sqlInit.getlogList(selval,inpval);
    })
})