var data = [
  {author: "Pete Hunt", text: "This is one comment"},
  {author: "Jordan Walke!", text: "This is *another* comment"}
];


var CommentBox = React.createClass({
	getInitialState: function() {
    	return {data: []};
  	},

  	componentDidMount: function() {
	    $.ajax({
	      url: this.props.url,
	      dataType: 'json',
	      cache: false,
	      success: function(data) {
	        this.setState({data: data});
	      }.bind(this),
	      error: function(xhr, status, err) {
	        console.error(this.props.url, status, err.toString());
	      }.bind(this)
	    });
  	},

    handleCommentSubmit: function(comment) {
        $.ajax({
            url: this.props.url,
            contentType: 'application/json',
            dataType: 'json',
            type: 'POST',
            data: JSON.stringify(comment),
            success: function(data) {
                this.setState({data: data});
            }.bind(this),
            error: function(xhr, status, err) {
                console.error(this.props.url, status, err.toString());
            }.bind(this)
        });
    },

	render: function() {
	    return (
	    	<div className="commentBox">
	        	<h1>Comments</h1>
	        	<CommentList data={this.state.data} />
	        	<CommentForm onCommentSubmit={this.handleCommentSubmit} />
	      	</div>
	    );
	}
});

var CommentList = React.createClass({
  render: function() {
  	var commentNodes = this.props.data.map(function (comment, i) {
      return (
        <Comment author={comment.author} key={i}>
          {comment.text}
        </Comment>
      );
    });

    return (
      <div className="commentList">
        {commentNodes}
      </div>
    );
  }
});

var CommentForm = React.createClass({

    handleSubmit: function(e) {
        e.preventDefault();
        var author = this.refs.author.value.trim();
        var text = this.refs.text.value.trim();
        if (!text || !author) {
          return;
        }

        this.props.onCommentSubmit({author: author, text: text});

        this.refs.author.value = '';
        this.refs.text.value = '';
        return;
    },

    render: function() {
        return (
            <div className="commentForm">
                <form className="commentForm" onSubmit={this.handleSubmit}>
                        <input type="text" placeholder="Your name" ref="author" />
                        <input type="text" placeholder="Say something..." ref="text" />
                        <input type="submit" value="Post" />
                </form>
            </div>
        );
    }
});

var Comment = React.createClass({
	rawMarkup: function() {
    	var rawMarkup = marked(this.props.children.toString(), {sanitize: true});
    	return { __html: rawMarkup };
  	},
  	render: function() {
    return (
      <div className="comment">
        <h2 className="commentAuthor">
          {this.props.author}
        </h2>
        <span dangerouslySetInnerHTML={this.rawMarkup()} />
      </div>
    );
  }
});

ReactDOM.render(
  <CommentBox url="/rest/api/mt/comment" />,
  document.getElementById('content')
);
