---
layout: default
title: StoRM News
---

# News

-----



{% for post in site.posts limit:1 %}
<div class="row-fluid marketing news-row">
    <div class="span2">
        <div class="calendar"> 
            <span class="month">{{ post.date || date: "%Y - %b" }} </span>
            <span class="day">{{ post.date || date: "%d" }}</span>
        </div>
    </div>
    <div class="span10">
        <h3><a href="{{ site.baseurl }}{{ post.url }}">{{post.title}}</a></h3>
        <p>{{post.summary}}</p>
        <a href="{{ site.baseurl }}{{ post.url }}">Read more</a>
    </div>
</div>
{% endfor %}



-----


{% for post in site.posts limit:6 offset:1 %}
<div class="row-fluid marketing news-row">
    <div class="span2">
        <p class="text-left">{{ post.date | date_to_long_string }}</p>
    </div>
    <div class="span10">
        <h4 style="margin-top: 0px;"><a href="{{ site.baseurl }}{{ post.url }}">{{post.title}}</a></h4>
        <p>{{post.summary}}</p>
    </div>
</div>
{% endfor %}

{% for post in site.posts limit:20 offset:7 %}
<div class="row-fluid marketing news-row" style="display: none;">
    <div class="span2">
        <p class="text-left">{{ post.date | date_to_long_string }}</p>
    </div>
    <div class="span10">
        <h4 style="margin-top: 0px;"><a href="{{ site.baseurl }}{{ post.url }}">{{post.title}}</a></h4>
        <p>{{post.summary}}</p>
    </div>
</div>
{% endfor %}

<a href="#" onclick="showallnews(this);">show older...</a>
