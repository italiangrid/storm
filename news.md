---
layout: default
title: StoRM News
---

# News

{% assign p_year = site.posts[0].date | date: "%Y" %}
<h4 class="marketing" style="border-bottom: 1px solid #efefef; padding-bottom: 14px; margin-bottom: 16px;">{{ p_year }}</h4>

{% for post in site.posts limit: 3 %}

{% assign c_year = post.date | date: "%Y" %}
{% assign author = site.data.authors[post.author] %}

{% if p_year > c_year %}
<h4 class="marketing" style="border-bottom: 1px solid #efefef; padding-bottom: 14px; margin-bottom: 16px;">{{ c_year }}</h4>
{% endif %}

<div class="row-fluid marketing news-row" style="border-bottom: 1px solid #efefef; padding-bottom: 14px; margin-bottom: 16px;">
  <div class="span2" style="padding-bottom: 15px;">
    <div class="calendar">
      <span class="month">{{ post.date | date: "%B" }} </span>
      <span class="day">{{ post.date | date: "%d" }}</span>
    </div>
  </div>
  <div class="span10">
    {% if post.tag == "release" %}
    <img src="{{site.baseurl}}/assets/images/bookmark-orange.png" style="width:20px; float: right; margin: 0 0;"/>
    {% endif %}
    <img class="media-object pull-left img-rounded" src="http://www.gravatar.com/avatar/{{author.gravatar}}?s=52" style="margin-right: 20px;">
    <h3><a href="{{site.baseurl}}{{post.url}}">{{post.title}}</a></h3>
    <p><i class="icon-user"></i> {{author.display_name}}</p>
    <p>{{post.summary}}</p>
    <a href="{{site.baseurl}}{{post.url}}">Read more</a>
  </div>
</div>

{% assign p_year = c_year %}

{% endfor %}


{% for post in site.posts limit: 20 offset: 3 %}

{% assign c_year = post.date | date: "%Y" %}
{% assign author = site.data.authors[post.author] %}

{% if p_year > c_year %}
<h4 class="marketing" style="display: none; border-bottom: 1px solid #efefef; padding-bottom: 14px; margin-bottom: 16px;">{{c_year}}</h4>
{% endif %}

<div class="row-fluid marketing news-row" style="display: none; border-bottom: 1px solid #efefef; padding-bottom: 14px; margin-bottom: 16px;">
  <div class="span2" style="padding-bottom: 15px;">
    <div class="calendar">
      <span class="month">{{ post.date | date: "%B" }} </span>
      <span class="day">{{ post.date | date: "%d" }}</span>
    </div>
  </div>
  <div class="span10">
    {% if post.tag == "release" %}
    <img src="{{site.baseurl}}/assets/images/bookmark-orange.png" style="width:20px; float: right; margin: 0 0;"/>
    {% endif %}
    <img class="media-object pull-left img-rounded" src="http://www.gravatar.com/avatar/{{author.gravatar}}?s=52" style="margin-right: 20px;">
    <h3><a href="{{site.baseurl}}{{post.url}}">{{post.title}}</a></h3>
    <p><i class="icon-user"></i> {{author.display_name}}</p>
    <p>{{post.summary}}</p>
    <a href="{{site.baseurl}}{{post.url}}">Read more</a>
  </div>
</div>

{% assign p_year = c_year %}

{% endfor %}

<a href="#" onclick="showallnews(this);">show older...</a>
