(function() {
    // 声明构造函数
    function Drag() {
        // 创建拖拽box(默认id为helpDocBox)
        this.el = $('#resContent')[0];

        // 开始拖动时,鼠标的位置
        this.startX = 0;
        this.startY = 0;
        // 开始拖动时,拖动元素的tanslate
        this.sourceX = 0;
        this.sourceY = 0;
        // 开始拖拽时,元素的宽和高以及drager
        this.width = 0;
        this.height = 0;
        this.drager = "";
        // 拖拽过程中上一次鼠标的位置
        this.dragX = 0;
        this.dragY = 0;
        this.init();
    }

    // 添加原型方法
    Drag.prototype = {
        constructor: Drag,
        init: function() {
            this.setDrag();
        },
        setDrag: function() {
            var self = this;
            // 绑定点击关闭事件
            $(self.el).find('.head span').on('click', function(event) {
                event.stopPropagation();
                $(self.el).css('display', 'none');
            });
            // 给head绑定拖拽位置监听
            function start(event) {

                $(self.el).attr('onselectstart', "return false;");

                self.startX = event.pageX;
                self.startY = event.pageY;

                var pos = self.getPosition();

                self.sourceX = pos.x;
                self.sourceY = pos.y;

                document.addEventListener('mousemove', move, false);
                document.addEventListener('mouseup', end, false);
            }

            function move(event) {
                var currentX = event.pageX;
                var currentY = event.pageY;

                var distanceX = currentX - self.startX;
                var distanceY = currentY - self.startY;

                self.setPosition({
                    x: (self.sourceX + distanceX).toFixed(),
                    y: (self.sourceY + distanceY).toFixed()
                })
            }

            function end(event) {
                $(self.el).removeAttr('onselectstart');
                document.removeEventListener('mousemove', move);
                document.removeEventListener('mouseup', end);
            }

            // 给八个拖拽点绑定拖拽尺寸监听
            $(self.el).find('.drager').on('mousedown', resizeStart);
            function resizeStart() {
                $(self.el).attr('onselectstart', "return false;");
                self.startX = event.pageX;
                self.startY = event.pageY;

                self.dragX = event.pageX;
                self.dragY = event.pageY;

                var pos = self.getPosition();

                self.sourceX = pos.x;
                self.sourceY = pos.y;

                self.width = self.getSize().w;
                self.height = self.getSize().h;

                document.addEventListener('mousemove', resizeMove, false);
                document.addEventListener('mouseup', resizeEnd, false);
            }

            function resizeMove(event) {
                var distanceX = event.pageX - self.dragX;
                var distanceY = event.pageY - self.dragY;

                self.drager = $(event.target).data("direct") ? $(event.target).data("direct") : self.drager;
                self.setSize({
                    x: distanceX.toFixed(),
                    y: distanceY.toFixed()
                });
                // 更新上一次拖拽鼠标的位置
                self.dragX = event.pageX;
                self.dragY = event.pageY;
            }

            function resizeEnd() {
                $(self.el).removeAttr('onselectstart');
                document.removeEventListener('mousemove', resizeMove);
                document.removeEventListener('mouseup', resizeEnd);
            }
        },
        getPosition: function() {
            var transformValue = document.defaultView.getComputedStyle(this.el, false)["transform"];
            if(transformValue == 'none') {
                return {x: 0, y: 0};
            } else {
                var temp = transformValue.match(/-?\d+/g);
                return {
                    x: parseInt(temp[4].trim()),
                    y: parseInt(temp[5].trim())
                }
            }
        },
        getSize: function() {
            var widthValue = document.defaultView.getComputedStyle(this.el, false)["width"];
            var heightValue = document.defaultView.getComputedStyle(this.el, false)["height"];
            return {w: parseInt(widthValue), h: parseInt(heightValue)};
        },
        setPosition: function(pos) {
            this.el.style["transform"] = 'translate('+ pos.x +'px, '+ pos.y +'px)';
        },
        setSize: function(pos) { // className: 拖拽类型(四边拖拽或者四角拖拽)
            var self = this;
            var pos = {x: parseInt(pos.x), y: parseInt(pos.y)};
            // 当前的拖拽位置
            var translateX = self.getPosition().x;
            var translateY = self.getPosition().y;
            var dh = $(document).height() - 100;
            switch (self.drager) {
                case "top":
                    if ((self.height - pos.y) >= 100 && (self.height - pos.y) <= dh ) {
                        self.height -= pos.y;
                        this.el.style["height"] = self.height + 'px';
                    }
                    break;
                default:
                    break;
            }
        }
    }
    // 暴露Drag类
    window.Drag = Drag;
})();