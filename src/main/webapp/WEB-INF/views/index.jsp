<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <title>DB MANAGER</title>
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <meta name="apple-mobile-web-app-status-bar-style" content="black">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="format-detection" content="telephone=no">

    <link rel="stylesheet" href="${ctx}/ui/layui/css/layui.css?t=1629677917965" media="all">
    <link rel="stylesheet" href="${ctx}/ui/codemirror/codemirror.css"/>
    <link rel="stylesheet" href="${ctx}/ui/css/sql.css"/>
</head>
<body>

<div class="layui-layout layui-layout-admin">
    <div class="layui-header">
        <div class="layui-logo layui-hide-xs ">DB MANAGER</div>
        <div class="sql-title" style="padding-left: 200px;margin-top: 5px">
            <select style="float: left">
                <option>网址</option>
                <option>可信</option>
            </select>
            <div class="layui-btn-group" style="float: left;padding-left:50px ">
                <button type="button" class="layui-btn layui-btn-primary layui-btn-xs"><i class="layui-icon">&#xe623;</i>运行</button>
                <button type="button" class="layui-btn layui-btn-primary layui-btn-xs"><i class="layui-icon">&#xe623;&#xe623;</i>批量更新</button>
            </div>
        </div>
        <ul class="layui-nav layui-layout-right">
            <li class="layui-nav-item layui-hide layui-show-md-inline-block">
                <a href="javascript:;">
                    徐先念
                </a>
                <dl class="layui-nav-child">
                    <dd><a href="">退出</a></dd>
                </dl>
            </li>
            <li class="layui-nav-item" lay-header-event="menuRight" lay-unselect>
                <a href="javascript:;">
                    <i class="layui-icon layui-icon-more-vertical"></i>
                </a>
            </li>
        </ul>
    </div>



    <div class="layui-body">
        <!-- 内容主体区域 -->
        <div>
            <textarea id="code"></textarea>
        </div>
        <div class="layui-tab" lay-filter="demo">
            <ul class="layui-tab-title">
                <li class="layui-this" lay-id="11">查询</li>
                <li lay-id="22">更新</li>
                <li lay-id="33">表操作</li>
            </ul>
            <div class="layui-tab-content">
                <div class="layui-tab-item layui-show">
                    <div class="sql-title">
                        <div class="layui-btn-group">
                            <button type="button" class="layui-btn layui-btn-primary layui-btn-xs"><i class="layui-icon"> &#xe625;</i>更多</button>
                            <button type="button" class="layui-btn layui-btn-primary layui-btn-xs"><i class="layui-icon"> &#xe669;</i>刷新</button>
                            <button type="button" class="layui-btn layui-btn-primary layui-btn-xs"><i class="layui-icon"> &#xe658;</i>收藏sql</button>
                            <button type="button" class="layui-btn layui-btn-primary layui-btn-xs"><i class="layui-icon"> &#xe641;</i>分享sql</button>
                            <button type="button" class="layui-btn layui-btn-primary layui-btn-xs"><i class="layui-icon"> &#xe67d;</i>导出</button>
                        </div>
                    </div>
                    <table class="layui-table" lay-size="sm">
                        <thead>
                        <tr>
                            <th>人物</th>
                            <th>民族</th>
                            <th>出场时间</th>
                            <th>格言</th>
                        </tr>
                        </thead>
                        <tbody>
                        <tr>
                            <td>贤心</td>
                            <td>汉族</td>
                            <td>1989-10-14</td>
                            <td>人生似修行</td>
                        </tr>
                        <tr>
                            <td>张爱玲</td>
                            <td>汉族</td>
                            <td>1920-09-30</td>
                            <td>于千万人之中遇见你所遇见的人，于千万年之中，时间的无涯的荒野里…</td>
                        </tr>
                        <tr>
                            <td>Helen Keller</td>
                            <td>拉丁美裔</td>
                            <td>1880-06-27</td>
                            <td> Life is either a daring adventure or nothing.</td>
                        </tr>
                        <tr>
                            <td>岳飞</td>
                            <td>汉族</td>
                            <td>1103-北宋崇宁二年</td>
                            <td>教科书再滥改，也抹不去“民族英雄”的事实</td>
                        </tr>
                        <tr>
                            <td>孟子</td>
                            <td>华夏族（汉族）</td>
                            <td>公元前-372年</td>
                            <td>猿强，则国强。国强，则猿更强！</td>
                        </tr>
                        </tbody>
                    </table>


                </div>
                <div class="layui-tab-item">
                    <div class="sql-title">
                        <span class="sql-tip">受影响数据1000条！</span>
                        <div class="layui-btn-group">
                            <button type="button" class="layui-btn layui-btn-primary layui-btn-xs"><i class="layui-icon"> &#xe605;</i>执行</button>
                        </div>
                    </div>
                    <table class="layui-table" lay-size="sm">

                        <thead>
                        <tr>
                            <th>ID</th>
                            <th>NAME</th>
                        </tr>
                        </thead>
                        <tbody>
                        <tr>
                            <td>2000011<span style="float:right;">...</span></td>
                            <td>徐先念 --><span style="color: green">徐先念1</span><span style="float:right;">...</span></td>

                        </tr>

                        </tbody>
                    </table>
                </div>
                <div class="layui-tab-item">
                    <div class="sql-title" style="border-bottom: 1px solid #eee">
                        <div class="layui-btn-group">
                            <button type="button" class="layui-btn layui-btn-primary layui-btn-xs"><i class="layui-icon"> &#xe658;</i>收藏sql</button>
                            <button type="button" class="layui-btn layui-btn-primary layui-btn-xs"><i class="layui-icon"> &#xe641;</i>分享sql</button>

                        </div>
                    </div>
                    <div class="sql-p10">执行成功：alter table A_20151 add column_2 int</div>
                </div>

            </div>
        </div>

    </div>
    <div class="layui-right">

        <div class="layui-side-scroll">
            <div class="sql-db">
                <select>
                    <option>全部</option>
                    <option>我的</option>
                    <option>分享</option>
                </select><input type="text" placeholder="输入关键字查询">
            </div>
            <table class="layui-table" lay-size="sm">

                <tbody>
                <tr>
                    <td>查询未实名数据 <span class="layui-badge layui-bg-red">胡晓彬</span>
                    </td>
                </tr>
                <tr>
                    <td>查询未实名数据</td>
                </tr>
                <tr>
                    <td>查询未实名数据</td>
                </tr>
                <tr>
                    <td>查询未实名数据</td>
                </tr>
                <tr>
                    <td>查询未实名数据</td>
                </tr>
                <tr>
                    <td>查询未实名数据</td>
                </tr>
                <tr>
                    <td>查询未实名数据</td>
                </tr>
                <tr>
                    <td>查询未实名数据</td>
                </tr>
                <tr>
                    <td>查询未实名数据</td>
                </tr>
                <tr>
                    <td>查询未实名数据</td>
                </tr>

                </tbody>
            </table>
        </div>
    </div>
</div>

<script src="${pageContext.request.contextPath}/ui/layui/layui.js?t=1629677917965" charset="utf-8"></script>
<script src="${ctx}/ui/codemirror/codemirror.js"></script>
<script src="${ctx}/ui/codemirror/matchbrackets.js"></script>
<script src="${ctx}/ui/codemirror/sql.js"></script>
<link rel="stylesheet" href="${ctx}/ui/codemirror/hint/show-hint.css"/>
<script src="${ctx}/ui/codemirror/hint/show-hint.js"></script>
<script src="${ctx}/ui/codemirror/hint/sql-hint.js"></script>

<script>

    var editor;
    window.onload = function () {
        editor = CodeMirror.fromTextArea(document.getElementById('code'), {
            mode: "text/x-plsql",
            indentWithTabs: true,
            smartIndent: true,
            lineNumbers: true,
            matchBrackets: true,
            autofocus: true

        });
        editor.setSize("", "500");
        editor.on("keyup", function (cm) {
            CodeMirror.showHint(cm, CodeMirror.hint.deluge, {completeSingle: false});
        });

    };


    function check() {
        alert(editor.getValue());
    }


</script>
</body>
</html>
