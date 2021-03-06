<% root = content.rootpath ?: '' %>
<div class="navbar navbar-default navbar-fixed-top" role="navigation">
  <a href="https://github.com/jamming/sabina">
    <img
      style="position: absolute; top: 0; right: 0; border: 0;"
      src=
        "https://camo.githubusercontent.com/38ef81f8aca64bb9a64448d0d70f1308ef5341ab/68747470733a2f2f73332e616d617a6f6e6177732e636f6d2f6769746875622f726962626f6e732f666f726b6d655f72696768745f6461726b626c75655f3132313632312e706e67"
      alt="Fork me on GitHub"
      data-canonical-src=
        "https://s3.amazonaws.com/github/ribbons/forkme_right_darkblue_121621.png">
  </a>
  <div class="container">
    <div class="navbar-header">
      <button
        type="button"
        class="navbar-toggle"
        data-toggle="collapse"
        data-target=".navbar-collapse">

        <span class="sr-only">Toggle navigation</span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
      </button>
      <a class="navbar-brand" href="${root}index.html">Sabina</a>
    </div>

    <div class="navbar-collapse collapse">
      <ul class="nav navbar-nav">
        <li><a href="${root}why.html">Why</a></li>
        <li><a href="${root}readme.html">Readme</a></li>
        <li><a href="${root}reference.html">Reference</a></li>
        <li><a href="${root}license.html">License</a></li>
        <li><a href="${root}contact.html">Contact</a></li>
        <li><a href="http://there4.co">there4.co</a></li>
      </ul>

      <ul class="nav navbar-nav navbar-right">
        <!-- Ugly hack for Twitter to display nicely... in Bootstrap :O -->
        <li style="margin-top: 12px; margin-left: 5px">
          <a
            href="https://twitter.com/share"
            class="twitter-share-button"
            data-text="Check Sabina... a framework you can hack!"
            data-via="jaguililla"
            data-count="none"
            data-dnt="true">

            Tweet
          </a>
          <script>
            !function(d,s,id){
            var js,fjs=d.getElementsByTagName(s)[0],p=/^http:/.test(d.location)?'http':'https';
            if(!d.getElementById(id)){js=d.createElement(s);
            js.id=id;js.src=p+'://platform.twitter.com/widgets.js';
            fjs.parentNode.insertBefore(js,fjs);}}(document, 'script', 'twitter-wjs');
          </script>
        </li>
      </ul>
    </div>
  </div>
</div>
<div class="container">
