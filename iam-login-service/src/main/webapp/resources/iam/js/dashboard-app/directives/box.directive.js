/*
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2016-2018
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
angular.module('dashboardApp').directive('box', function() {
	return {
		restrict : 'C',
		compile : function(tElement, tAttr, transclude) {
			var _this = this;
			$(tElement).find(this.boxWidgetOptions.boxWidgetSelectors.collapse)
					.click(function(e) {
						e.preventDefault();
						_this.collapse($(this));
					});
			$(tElement).find(this.boxWidgetOptions.boxWidgetSelectors.remove)
					.click(function(e) {
						e.preventDefault();
						_this.remove($(this));
					});
		},
		collapse : function(element) {
			// Find the box parent
			var box = element.parents(".box").first();
			// Find the body and the footer
			var bf = box.find(".box-body, .box-footer");
			if (!box.hasClass("collapsed-box")) {
				// Convert minus into plus
				element.children(".fa-minus").removeClass("fa-minus").addClass(
						"fa-plus");
				bf.slideUp(300, function() {
					box.addClass("collapsed-box");
				});
			} else {
				// Convert plus into minus
				element.children(".fa-plus").removeClass("fa-plus").addClass(
						"fa-minus");
				bf.slideDown(300, function() {
					box.removeClass("collapsed-box");
				});
			}
		},
		remove : function(element) {
			// Find the box parent
			var box = element.parents(".box").first();
			box.slideUp();
		},
		boxWidgetOptions : {
			boxWidgetIcons : {
				// The icon that triggers the collapse event
				collapse : 'fa fa-minus',
				// The icon that trigger the opening event
				open : 'fa fa-plus',
				// The icon that triggers the removing event
				remove : 'fa fa-times'
			},
			boxWidgetSelectors : {
				// Remove button selector
				remove : '[data-widget="remove"]',
				// Collapse button selector
				collapse : '[data-widget="collapse"]'
			}
		}

	}
});