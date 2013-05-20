#!/usr/bin/perl
use strict;
use warnings;
use Data::Dumper;
use English '-no_match_vars';
use Cwd qw/getcwd abs_path/;
use File::Basename;

{
package CLI_Utils;
#
# This package has been inserted in this code for easy portability.
# The main code starts ~ 700 lines below
#

use strict;
use warnings;
use English '-no_match_vars';
use Getopt::Long;
use Data::Dumper;
use File::Basename;

require Exporter;

our @ISA = qw(Exporter);

our $VERSION = '0.01';
our $VERBOSE = $ENV{CLI_VERBOSE} || 0;
our $DEBUG = $ENV{CLI_DEBUG} || 0;

my %default_options = (
    # cgi => {
    #        parse   => 'cgi',
    #        value   => 0,
    #        so      => 900,
    #        groups  => ['all', 'cli-admin'],
    #        help    => ['Generate a CGI form to edit this program options ' ]
    #    },
     verbose => {
            parse   => 'verbose',
            value   => 0,
            so      => 910,
            groups  => ['all', 'cli-admin'],
            help    => ['Show more information during installation and help ' ]
        },
     manual => {
            parse   => 'man|manual',
            value   => 0,
            so      => 920,
            groups  => ['all', 'cli-admin'],
            help    => ['Display the program manual' ]
        },
     version => {
            parse   => 'v|version',
            value   => 0,
            so      => 930,
            groups  => ['all', 'cli-admin'],
            help    => ["Show $PROGRAM_NAME version and exit " ]
        },
     display_options => {
            parse   => 'display-options',
            value   => 0,
            so      => 940,
            groups  => ['all', 'cli-admin'],
            help    => ["Show all the options without running the program" ]
        },
      help => {
            parse   => 'h|help',
            value   => 0,
            so      => 1000,
            groups  => ['all', 'cli-admin'],
            help    => ['Display this help' ]
        },
) ;

sub new
{
    my ($class, $params ) = @_;
    my $main_option;
    my $program_name;
    if ($params)
    {
       if (ref($params))
       {
           if (ref($params) eq 'HASH')
            {
                $program_name =  $params->{program_name};
                $main_option  =  $params->{main_option};
                unless ($program_name)
                {
                    $program_name = basename($PROGRAM_NAME);
                }
            }
            elsif (ref($params) eq 'ARRAY')
            {
                $program_name = $params->[0];
                $main_option  = $params->[1];
            }
            else
            {
                die "can't deal with 'params' of this type\n";
            }
        }
        else  # scalar
        {
            $program_name = $params;
        }
    }
    my $self = bless {
        parse_options => \%default_options,
        options => {},
        main_option => $main_option,
        program_name => $program_name,
    }, $class;
    return $self;
}

# Returns a nicely formatted set of options for a command with a long list of arguments
sub pretty_command
{
    my ($cmd, $args) = @_;
    my $indent = ' ' x 4;
    $args =~ s{\s+-}{ \\\n$indent-}g;
    return "$cmd$args\n";
}


sub display_options
{
    my ($self) = @_;
    print $self->get_command_line();
}
 

sub write_options
{
    my ($self) = @_;
    my $cli_history = './cookbook/tungsten-cookbook-command-line.history';
    open my $FH, '>>', $cli_history
        or die "can't write to $cli_history($!)\n";
    print $FH "# ", scalar( localtime), "\n";
    $self->{options}{skip_zeroes}=1;
    $self->{options}{skip_defaults}=1;
    print $FH $self->get_command_line(), "\n";
    $self->{options}{skip_zeroes}=0;
    $self->{options}{skip_defaults}=0;
    close $FH;
}
 
sub get_command_line
{
    my ($self) = @_;
    my $parse_options = $self->{parse_options};
    my $options = $self->{options};
    my $command_line = '';
    for my $op (
                sort { $parse_options->{$a}{so} <=> $parse_options->{$b}{so} }
                grep { $parse_options->{$_}{parse}}  keys %{ $parse_options }
               )
    {
        next unless defined $options->{$op};
        next if $op eq 'skip_zeroes';
        next if $op eq 'skip_defaults';
        if ($options->{skip_zeroes})
        {
            next unless $options->{$op};
        }
        if ($options->{skip_defaults})
        {
            next if $options->{$op} && $parse_options->{$op}{value} && ($options->{$op} eq $parse_options->{$op}{value});
        }
        my $param =  $parse_options->{$op}{parse};
        my (undef, $long ) = $param =~ / (?: (\w+) \| )? ([^\|=]+) /x;
        if (ref($options->{$op}) && (ref($options->{$op}) eq 'ARRAY'))
        {
            $command_line .= ' --' . $long . '=' . "@{$options->{$op}}"; 
        }
        else
        {
            if ($parse_options->{$op}{parse} =~ /=/)
            {
                $command_line .= ' --' . $long . '=' . $options->{$op}; 
            }
            else
            {
                $command_line .= ' --' . $long ; 
            }
        }
    }
    # print Dumper $options, $parse_options;
    return pretty_command($PROGRAM_NAME, $command_line);
}

sub process_cgi
{
    my ($self) = @_;
    my $parse_options = $self->{parse_options};
    eval "use CGI qw/:standard *table/; use CGI::Pretty qw(:html3)";
    if ($CHILD_ERROR)
    {
        die "Can't load the CGI module\n";
    }
    my $cgi = CGI::Pretty->new();
    if ($cgi->param())
    {
     
    }
    else
    {
        my $CGI_text = $cgi->header() 
            . $cgi->start_html($self->{program_name} || $PROGRAM_NAME)
            . $cgi->h1($self->{program_name} || $PROGRAM_NAME) 
            . $cgi->start_form()
            . start_table( {border => '1', cellpadding => 5, cellspacing=> 0});
        for my $op (
                sort { $parse_options->{$a}{so} <=> $parse_options->{$b}{so} }
                grep { $parse_options->{$_}{parse}}  keys %{ $parse_options }
               )
        {
           my $parse = $parse_options->{$op}{parse} ;

           my (undef, $long ) = $parse =~ / (?: (\w+) \| )? ([^\|=]+) /x;

           if ($parse_options->{$op}{allowed})
           {
                $CGI_text .= 
                    Tr(td(
                    b($long),
                     p(), 
                    radio_group(
                    -name   =>  $op, 
                    -values =>  [ keys %{ $parse_options->{$op}{allowed} } ],
                    -linebreak => 'true',
                    -default => $self->{options}{$op} || $parse_options->{$op}{value})  
                    ),
                    td(i( join ' ', @{ $parse_options->{$op}{help} } ))
                    )
                . p(); 
           }
           elsif ($parse =~ /=[si]/) 
           {
                $CGI_text   .= 
                            Tr(td(
                            b($long), 
                             ' ',
                            textfield (
                                -name  => $op,
                                -value =>$self->{options}->{$op} || $parse_options->{$op}{value} ) 
                             ),
                             td(i( join ' ', @{ $parse_options->{$op}{help} } ))
                             )
                            . p();
           }
           else 
           {
               $CGI_text .= 
                            Tr(td(
                            checkbox(
                                -name => $op,
                                -label => $long, 
                                -checked => $self->{options}{$op} || $parse_options->{$op}{value} ? '1' : '0',
                                )
                            ),
                            td( i(join ' ', @{ $parse_options->{$op}{help} }) )
                            )
                            . p()
           }
        }
        $CGI_text .=  end_table()
                     . submit(-name => 'submit', -value => "get $self->{program_name} options")
                     . end_form() . hr() . end_html();
        print $CGI_text; 
    } 
    exit;
}


sub getoptions
{
    my ($self) = @_;
    my $parse_options = $self->{parse_options};
    if ($self->{program_name})
    {
        my $prefix = $self->{program_name};
        $prefix =~ s/\W/_/g; 
        for my $op (keys %{ $parse_options} )
        {
            my $key = uc "${prefix}_$op";
            if ($ENV{$key})
            {
                $parse_options->{$op}{value} = $ENV{$key};
            }
        } 
    }
    my %options = map { $_ ,  $parse_options->{$_}{'value'}}  keys %{$parse_options};
    $self->before_parsing();
    GetOptions (
        map { $parse_options->{$_}{parse}, \$options{$_} }    
        grep { $parse_options->{$_}{parse}}  keys %{$parse_options} 
    ) or $self->get_help('');

    $self->{options}= \%options;
    if ($options{cgi})
    {
        $self->process_cgi();
    }
    if ($options{display_options})
    {
        $self->display_options();
        exit 0;
    }
    if ($options{version})
    {
        print $self->get_credits();
        exit 0;
    }
    $VERBOSE = $options{verbose} if $options{verbose};
    $self->get_help() if $options{help};
    get_manual()      if $options{manual};
    $self->after_parsing();
    $self->validate();
}

my %options_fields = 
(
    parse       => 1, 
    help        => 1,
    so          => 1,
    value       => 0,
    short       => 0,
    long        => 0,
    must_have   => 0,
    allowed     => 0,
    groups      => 0,
    display     => 0,
    hide        => 0,
);

sub add_option 
{
    my ($self, $option_name, $option, $replace) = @_;
    unless ($option_name)
    {
        die "Option_name parameter required for add_option\n";
    }

    if ($replace && (! $self->{parse_options}{$option_name}))
    {
        die "Option '$option_name' does not exist: Can't replace.\n";
    }

    if ($self->{parse_options}{$option_name} && (! $replace ))
    {
        die "Option '$option_name' already exists\n";
    }

    unless ($option)
    {
        die "Option parameter required for add_option\n";
    }

    if (! ref($option) or (ref($option) ne 'HASH'))
    {
        die "The 'option' parameter must be a hash ref\n";
    }

    for my $field (keys %{ $option} )
    {
        die "unrecognized field '$field' \n" unless exists $options_fields{$field};
    }

    if ($option->{short} || $option->{long})
    {
        if ($option->{parse})
        {
            die "You must provide either 'parse' or 'short' and 'long', but not both\n";
        }
        $option->{parse} = $option->{short} . '|' . $option->{long};
    }

    for my $field (grep {$options_fields{$_}} keys %options_fields)
    {
        die "field '$field' must exist in option\n" unless exists $option->{$field};
    }

    if (! ref $option->{help})
    {
        $option->{help} = [$option->{help}];
    }

    if (ref($option->{help}) ne 'ARRAY')
    {
        die "the 'help' field in option $option_name must be an array of strings\n";
    }

    if ($option->{allowed})
    {
        my $allowed = $option->{allowed};
        if (ref $allowed )
        {
            if (ref $allowed eq 'ARRAY')
            {
                my %new_allowed;
                for my $f (@{ $option->{allowed} })
                {
                    $new_allowed{$f} = 1;
                }
                $option->{allowed} = \%new_allowed;
            }
        }
        else
        {
            $option->{allowed} = { $allowed => 1};
        }
    }
    $self->{parse_options}{$option_name} = $option;
    return $self;
}

sub validate
{
    my ($self) = @_;
    my ($options, $parse_options) = ($self->{options}, $self->{parse_options});
    my @to_be_defined;
    my @not_allowed;
    my $must_exit = 0;
    #
    # Checks that required options are filled
    #
    for my $must ( grep {$parse_options->{$_}->{must_have}} keys %{$parse_options})
    {
        unless (defined $options->{$must})
        {
            my $required = 0;
            if ( ! $self->{main_option} 
                && 
                ref($parse_options->{$must}->{must_have}) 
                && 
                ref($parse_options->{$must}->{must_have}) eq 'ARRAY' )
            {
                warn  "The option $must was defined as depending on a set of values\n"
                    . "(@{$parse_options->{$must}->{must_have}})\n"
                    . "but the 'main_option' label was not set in the constructor\n"; 
                $must_exit = 1;
            }
 
            if ($self->{main_option} 
                && 
                ref($parse_options->{$must}->{must_have}) 
                && 
                ref($parse_options->{$must}->{must_have}) eq 'ARRAY' )
            # 
            # Conditional requirement, with a list of tasks where it is required
            # Using information in the parsing options, this loop determines if 
            # some options must be filled or not.
            {
                for my $task (@{$parse_options->{$must}->{must_have}})
                {
                    # print Dumper($self->{main_option}, $task);
                    if (($self->{main_option}) 
                        &&  
                        ($options->{$self->{main_option}} )
                        &&  
                        ($task eq $options->{$self->{main_option}}))
                    {
                        $required = 1;
                    }
                }
            }
            elsif ($parse_options->{$must}->{must_have} eq '1')
            # unconditional requirement
            {
                $required=1;
            }
            push @to_be_defined, $must if $required;
        }
    }

    #
    # Checks that options requiring given keywords are not using anything different
    #
    for my $option (keys %{$options} ) {
        if (exists $parse_options->{$option}{allowed} && $options->{$option})
        {
            if (ref($options->{$option}) && ref($options->{$option}) eq 'ARRAY')
            {
                for my $item (@{$options->{$option}})
                {
                    unless (exists $parse_options->{$option}{allowed}{$item})
                    {
                        push @not_allowed, "Not allowed value '$item' for option '$option' - "
                        . " (Choose among: { @{[keys %{$parse_options->{$option}{allowed}} ]} })\n";
                    }
                }
            }
            else
            {
                unless (exists $parse_options->{$option}{allowed}{$options->{$option}})
                {
                    push @not_allowed, "Not allowed value '$options->{$option}' for option '$option' - "
                    . " (Choose among: { @{[keys %{$parse_options->{$option}{allowed}} ]} })\n";
                }
            }
        }
    }
    #
    # Reports errors, if any
    #
    if (@to_be_defined)
    {
        for my $must (@to_be_defined)
        {
            print "Option '$must' must be defined\n"
        }
    }
    if (@not_allowed)
    {
        for my $na (@not_allowed) 
        {
            print $na;
        }
    }
    if (@not_allowed or @to_be_defined or $must_exit)
    {
        exit 1;
    }
}
 
sub get_layout
{
    my $self = (@_);
    return '[options] operation';
}

sub get_help {
    my ($self, $msg) = @_;
    my $parse_options = $self->{parse_options};
    if ($msg) {
        warn "[***] $msg\n\n";
    }

    my $layout = $self->get_layout();
    my $HELP_MSG = q{};
    for my $op (
                sort { $parse_options->{$a}{so} <=> $parse_options->{$b}{so} }
                grep { $parse_options->{$_}{parse}}  keys %{ $parse_options }
               )
    {
        my $param =  $parse_options->{$op}{parse};
        my $param_str = q{    };
        my ($short, $long ) = $param =~ / (?: (\w+) \| )? (\S+) /x;
        if ($short)
        {
            $param_str .= q{-} . $short . q{ };
        }
        $long =~ s/ = s \@? / = name/x;
        $long =~ s/ = i / = number/x;
        $param_str .= q{--} . $long;
        $param_str .= (q{ } x (40 - length($param_str)) );
        my $text_items = $parse_options->{$op}{help};
        my $item_no=0;
        for my $titem (@{$text_items})
        {
            $HELP_MSG .= $param_str . $titem ;
            if (++$item_no == @{$text_items})
            {
                if ($VERBOSE && $parse_options->{$op}{value}) 
                {
                    if (length($parse_options->{$op}{value}) > 40)
                    {
                        $HELP_MSG .= "\n" . q{ } x 40;
                    }
                    $HELP_MSG .=  " ($parse_options->{$op}{value})";
                }
            }
            $HELP_MSG .= "\n";
            $param_str = q{ } x 40;
        }
        if ($VERBOSE && $parse_options->{$op}{must_have}) 
        {
            if (ref$parse_options->{$op}{must_have})
            {
                $HELP_MSG .=  (q{ } x 40) . "(Must have for: @{[join ',', sort @{$parse_options->{$op}{must_have}}  ]})\n"
            }
            else 
            {
                $HELP_MSG .= (q{ } x 40) . '(Must have)' . "\n";
            }
        }
        if ($VERBOSE && $parse_options->{$op}{allowed}) 
        {
            $HELP_MSG .=  (q{ } x 40) . "(Allowed: {@{[join '|', sort keys %{$parse_options->{$op}{allowed}}  ]}})\n"
        }
   }

   print $self->get_credits(),
          "Syntax: $self->{program_name} $layout \n",
          $HELP_MSG;
    exit( defined $msg );
}
 
sub get_manual
{
    my $perldoc = which('perldoc');
    if ($perldoc)
    {
        exec "perldoc $PROGRAM_NAME";
    }
    else
    {
        die  "The 'perldoc' program was not found on this computer.\n"
            ."You need to install it if you want to see the manual\n";
    }
}

#
# Custom implementation of the 'which' command.
# Returns the full path of the command being searched, or NULL on failure.
#
sub which
{
    my ($executable) = @_;
    if ( -x "./$executable" )
    {
        return "./$executable";
    }
    for my $dir ( split /:/, $ENV{PATH} )
    {
        $dir =~ s{/$}{};
        if ( -x "$dir/$executable" )
        {
            return "$dir/$executable";
        }
    }
    return;
}

sub resolveip
{
    my ($hostname) = @_;
    if ($hostname =~ /^\d+\.\d+\.\d+\.\d+$/)
    {
        return $hostname;
    }
    my $resolveip = which('resolveip');
    if ($resolveip)
    {
        my $ip = qx/$resolveip -s $hostname/;
        chomp $ip;
        return $ip;
    }
    my @lines = slurp('/etc/hosts');
    for my $line (@lines)
    {
        if ($line =~ /^\s*(\d+\.\d+\.\d+\.\d+).*\b$hostname\b/)
        {
            return $1;
        }
    }
    die "can't resolve IP for $hostname\n";
}


sub get_credits
{
    my ($self) = @_;
    return "Should override 'get_credits'\n";
}   

sub before_parsing
{
    my ($self) = @_;
    # warn "Should override 'before_parsing'\n";
}

sub after_parsing
{
    my ($self) = @_;
    #warn "Should override 'after_parsing'\n";
}

sub slurp
{
    my ($filename) = @_;
    open my $FH , '<', $filename
        or die "can't open $filename\n";
    my @lines = <$FH>;
    close $FH;
    if (wantarray)
    {
        return @lines;
    }
    else
    {
        my $text ='';
        $text .= $_ for @lines;
        chomp $text;
        return $text;
    }
}

sub get_cfg
{
    my ($fname) = @_;
    my $cfg = slurp($fname);
    $cfg =~ s/:/=>/g;
    $cfg = '$cfg=' . $cfg;
    eval $cfg;
    if ($@)
    {
        die "error evaluating contents of $fname\n";
    }
    return $cfg;
}

1;
} # end package CLI_Utils



{
package ConcurrentEvaluator;
my $VERSION = '1.0.1';
use base qw(CLI_Utils);

sub get_credits
{
    return   "Concurrent Evaluator Wrapper\n"
           . "version $VERSION\n"
           . "(C) Continuent, Inc, 2012-2013\n";
}

sub get_layout
{
    return   "[options] {start|stop|status} [{database|all}]";
}


} # end package ConcurrentEvaluator

package main;

my $cli = ConcurrentEvaluator->new(
    {
        program_name => 'concurrent_evaluator',
        main_option  => undef,
    }
);


$cli->add_option(
    database_name => {
        parse => 'd|database=s',
        value => 'evaluator',
        so    => 10,
        help    => ['database where we run the evaluator ']
    }
);

$cli->add_option(
    host => {
        parse => 'host=s',
        value => '127.0.0.1',
        so    => 15,
        help    => ['Host where the database server is running']
    }
);

$cli->add_option(
    user => {
        parse => 'u|user=s',
        value => 'tungsten_testing',
        so    => 20,
        help    => ['database user ']
    }
);

$cli->add_option(
    password => {
        parse => 'p|password=s',
        value => 'private',
        so    => 30,
        help    => ['database password ']
    }
);

$cli->add_option(
    port => {
        parse => 'port=i',
        value => 3306,
        so    => 40,
        help    => ['port used to connect to the database']

    }
);

$cli->add_option(
    table_prefix => {
        parse => 't|table-prefix=s',
        value => 'tbl',
        so    => 50,
        help    => ['prefix name to create evaluator tables'],
    }
);

$cli->add_option(
    config_file => {
        parse => 'c|config-file=s',
        value => undef,
        so    => 60,
        help    => ['File containing evaluator settings '],
    }
);

$cli->add_option(
    status_directory => {
        parse => 's|status-directory=s',
        value => undef,
        so    => 61,
        help    => ['Where the pid and log files will be stored '],
    }
);

$cli->add_option(
    log_file => {
        parse => 'log-file=s',
        value => undef,
        so    => 62,
        help    => ['File where the evaluator output will be stored '],
    }
);


$cli->add_option(
    process_file_prefix => {
        parse => 'x|process-file-prefix=s',
        value => 'evaluator',
        so    => 63,
        help    => ['default name for evaluator files '],
    }
);



$cli->add_option(
    continuent_root => {
        parse => 'r|continuent-root=s',
        value => $ENV{CONTINUENT_ROOT},
        must_have => 1,
        so    => 70,
        help    => ['Where tungsten is installed']
    }
);

$cli->add_option(
    mysql_bin => {
        parse => 'm|mysql-bin=s',
        value => '/usr/bin',
        so    => 80,
        help    => ['Where mysql binaries are installed']
    }
);

$cli->add_option(
    test_duration => {
        parse => 'test-duration=i',
        value => 3600,
        so    => 90,
        help    => ['How long the test will last']
    }
);

$cli->add_option(
    table_size => {
        parse => 'table-size=i',
        value => 100,
        so    => 100,
        help    => ['How big is the starting table ']
    }
);

$cli->add_option(
    inserts => {
        parse => 'inserts=i',
        value => 20,
        so    => 101,
        help    => ['How many inserts in the test ']
    }
);

$cli->add_option(
    updates => {
        parse => 'updates=i',
        value => 15,
        so    => 102,
        help    => ['How many updates in the test ']
    }
);

$cli->add_option(
    deletes => {
        parse => 'deletes=i',
        value => 15,
        so    => 103,
        help    => ['How many deletes in the test ']
    }
);


$cli->add_option(
    read_size => {
        parse => 'read-size=i',
        value => 2,
        so    => 104,
        help    => ['How many rows will be read in the test ']
    }
);

$cli->add_option(
    ramp_up_interval => {
        parse => 'ramp-up-interval=i',
        value => 0,
        so    => 105,
        help    => ['How long to wait before starting the next group of threads']
    }
);

$cli->add_option(
    ramp_up_increment => {
        parse => 'ramp-up-increment=i',
        value => 0,
        so    => 106,
        help    => ['How many threads to start in the rampUpIncrement period.']
    }
);

$cli->add_option(
    thread_count => {
        parse => 'thread-count=i',
        value => 10,
        so    => 110,
        help    => ['How many threads we will create ']
    }
);


$cli->add_option(
    instances => {
        parse => 'instances=i',
        value => 0,
        so    => 120,
        help    => ['How many instances of evaluator should we spawn']
    }
);

$cli->add_option(
    delay => {
        parse => 'delay=i',
        value => 0,
        so    => 125,
        help    => ['How long it waits after spawing each instance, before installing the next one']
    }
);

$cli->add_option(
    remove => {
        parse => 'remove=s@',
        value => undef,
        allowed => [qw(logs database all)],
        so    => 130,
        help    => ['Used with the "stop" command, it makes the program drop the evaluator installed elements (logs, databases)'],
    }
);

$cli->add_option(
    add_sentinel_tables => {
        parse => 'add-sentinel-tables',
        value => undef,
        so    => 140,
        help    => [
                        'Adds "first_table" and "last_table" to each database created.',
                        'This is useful to calculate the total time of replication.'
                   ],
    }
);

$cli->getoptions();

$cli->{options} = check_completeness ($cli->{options});

my $command = shift
    or $cli->get_help('missing command');

my $command_qualifier = shift || 'all';

# print Dumper $cli->{options}; exit;


if ($command eq 'start')
{
    my $database_prefix = $cli->{options}{database_name};
    if ($cli->{options}{instances})
    {
        my $process_file_prefix = $cli->{options}{process_file_prefix};
        for my $N (1 .. $cli->{options}{instances})
        {
            $cli->{options}{config_file}         = undef;
            $cli->{options}{database_name}       = "$database_prefix$N";
            $cli->{options}{process_file_prefix} = "$process_file_prefix$N";
            my $pid = start_evaluator($cli->{options});
            if ($pid && $cli->{options}{delay} && ($N < $cli->{options}{instances} ))
            {
                $|++;
                print "# Applying $cli->{options}{delay} seconds delay ";
                for my $delay ( 1 .. $cli->{options}{delay})
                {
                    print " $delay " if (($delay % 10) == 0);
                    sleep 1;
                }
                print " !\n";
            }
        }
    }
    else
    {
        start_evaluator($cli->{options});
    }
    if ($cli->{options}{add_sentinel_tables})
    {

my $query =<<END_QUERY;
select 
    CASE 
        WHEN count_first_table = 0 THEN "LOADING" 
        WHEN (count_first_table = count_last_table) THEN "COMPLETED" 
        ELSE "RUNNING" 
    END as load_status,
    elapsed
from (
  select 
    (select count(*) from information_schema.tables where table_name='first_table' and table_schema like '$database_prefix%') as count_first_table, 
    (select count(*) from information_schema.tables where table_name='last_table' and table_schema like '$database_prefix%') as count_last_table, 
    timediff(
        COALESCE((select 
            max(create_time) 
         from information_schema .tables 
         where 
            table_schema like '$database_prefix%' 
            and table_name='last_table'
        ), NOW()), 
        (select 
            min(create_time) 
         from information_schema .tables 
         where 
            table_schema like '$database_prefix%' 
            and table_name='first_table'
        )
    ) as elapsed 
) as t;

END_QUERY

        print "Sentinel tables were added to the evaluator database\n";
        print "Run the following query in the master and in the slaves to measure the total elapsed time of the load:\n",
            $query;
    }
}
elsif ($command eq 'stop')
{
    stop_evaluator($cli->{options}, $command_qualifier)
}
elsif ($command eq 'status')
{
    show_evaluator_status($cli->{options}, $command_qualifier)
}
else
{
    $cli->get_help("unknown command '$command'");
}


sub check_completeness
{
    my ($options) = @_;
    unless ($options->{mysql_bin} && (-x "$options->{mysql_bin}/mysql"))
    {
        my $mysql = CLI_Utils::which('mysql');
        if ($mysql)
        {
            $options->{mysql_bin} = dirname($mysql);
        }
        else
        {
            die "could not find mysql. Please use option --mysql-bin to set the correct path\n";
        }
    }
    $options->{continuent_root} =~ s{/$}{};
    unless ( -d $options->{continuent_root})
    {
        die "Installation directory not found '$options->{continuent_root}'\n";
    }
    my $evaluator_path = "$options->{continuent_root}/tungsten/bristlecone/bin/evaluator.sh";
    unless ( -x $evaluator_path)
    {
        die "# Could not find the evaluator launcher at '$evaluator_path'\n";
    }
    my $writing_directory = dirname($evaluator_path);
    $writing_directory =~ s{/bin/?$}{/share};

    unless ($options->{status_directory})
    {
        $options->{status_directory} = $writing_directory;
    }
    unless ( -d $options->{status_directory} )
    {
        mkdir $options->{status_directory} ;
        unless ( -d  $options->{status_directory} )
        {
            die "unable to create '$options->{status_directory}'\n";
        }
    }
    if ($options->{remove} && @{$options->{remove}})
    {
        my @remove_items = @{$options->{remove}};
        $options->{remove} = undef;
        for my $item (@remove_items)
        {
            $options->{remove}{$item} = 1;
        }

        if ($options->{remove}{all})
        {
            $options->{remove} = {logs => 1, database => 1};
        }
    }
    return $options;
}

sub get_evaluator_list
{
    my ($options) = @_;
    my $evaluator_path = "$options->{continuent_root}/tungsten/bristlecone/bin/evaluator.sh";
    my $writing_directory = dirname($evaluator_path);
    $writing_directory =~ s{/bin/?$}{/share};
    my $status_directory = $options->{status_directory} || $writing_directory;
    my @pid_files = glob("$status_directory/*.pid");
    return @pid_files
}

sub show_evaluator_status
{
    my ($options) = @_;
    my @pid_files = get_evaluator_list($options);
    for my $pid_file (@pid_files)
    {
        my $job_file = $pid_file;
        $job_file =~ s/\.pid$/.job/;
        if ( -f $job_file)
        {
            my $pid = CLI_Utils::slurp($pid_file);
            my @job_lines = CLI_Utils::slurp($job_file);
            my $config_file;
            for my $line (@job_lines)
            {
                if ($line =~ /^Using\s+:\s+(.*)/)
                {
                    $config_file = $1;
                }
            }
            if ($config_file)
            {
                $options->{config_file}      = basename($config_file);
                $options->{status_directory} = dirname($config_file);
            }
            else
            {
                die "could not find a config file in $job_file\n";
            }
            my ($pid_file_found, $checked_pid, $running) =get_evaluator_pid($options);
            # print " ($pid_file_found, $checked_pid, $running)\n";
            if ($checked_pid eq $pid)
            {
                print "Evaluator started with pid $pid ";
                if ($running)
                {
                    print "(running)\n";
                }
                else
                {
                    print " (stopped)\n";
                }
                if ($options->{verbose})
                {
                    print "@job_lines\n\n";
                }
            }
            else
            {
                die "PID mismatch using $pid_file and $job_file\n";
            }
        }
    } 
}

sub get_evaluator_pid
{
    my ($options) = @_;
    my $pid;
    my $active_pids_text;
    #my $extended_active_pids_text;
    my $pid_file_found = 0;
    # Looks for an existing pid file with a given name
    my $pid_file = "$options->{status_directory}/$options->{config_file}";
    $pid_file =~ s/.xml/.pid/;
    if ( -f $pid_file)
    {
        $pid_file_found =1;
        # print "PID FILE FOUND\n";
        $pid = CLI_Utils::slurp($pid_file);
        #
        # If the file was found, looks for a running PID with the same number as the one in the file
        $active_pids_text = qx/ps aux | grep Evaluator| grep -v "grep Evaluator"|awk '{print \$2}'/;
        #$extended_active_pids_text = qx/ps aux | grep Evaluator| grep -v "grep Evaluator"/;
    }
    else
    {
        $active_pids_text = qx/ps aux | grep Evaluator| grep -v "grep Evaluator"| grep '$options->{status_directory}.$options->{config_file}'| awk '{print \$2}'/ ;
        #$extended_active_pids_text = qx/ps aux | grep Evaluator| grep '$options->{config_file}'/ ;
    }
    #print "++ $extended_active_pids_text ++\n";
    my @active_pids = split /\n/, $active_pids_text;
    # if the PID exists and it is running, returns it.
    unless ($pid)   # there was no pid file
    {
        if (scalar @active_pids)  # but there is a process running with that XML file
        {
            # printf ">> %s %s %s\n", $pid_file_found || 'not-found', $active_pids[0], 1;
            return (    $pid_file_found,
                        $active_pids[0], #pid
                        1                # running
            );
        }
        else
        {
            # printf ">> %s %s %s\n", $pid_file_found || 'not-found', 0, 0;
            return ($pid_file_found, undef, undef);
        }
    }
    if (grep {$pid eq $_} @active_pids)
    {
        # printf ">> %s %s %s\n", $pid_file_found || 'not-found', $pid, 1;
        return ($pid_file_found, $pid, 1);
    }
    else
    {
        # printf ">> %s %s %s\n", $pid_file_found || 'not-found', $pid, 0;
        return ($pid_file_found, $pid, 0);
    }
}

sub stop_evaluator
{
    my ($options, $what) = @_;
    my @pid_files = get_evaluator_list($options);
    # print Dumper $options, \@pid_files; 
    for my $pid_file (@pid_files)
    {
        # print "++ $pid_file\n";
        my $job_file = $pid_file;
        my $xml_file = $pid_file;
        my $log_file = $pid_file;
        $job_file =~ s/\.pid$/.job/;
        $xml_file =~ s/\.pid$/.xml/;
        $log_file =~ s/\.pid$/.log/;
        if ( -f $job_file)
        {
            my $pid = CLI_Utils::slurp($pid_file);
            my @job_lines = CLI_Utils::slurp($job_file);
            my $config_file;
            my $database;
            my $host;
            my $port;
            for my $line (@job_lines)
            {
                if ($line =~ /^Log\s+:\s+(.*)/)
                {
                    $log_file = $1;
                }
                if ($line =~ /^Using\s+:\s+(.*)/)
                {
                    $config_file = $1;
                }
                if ($line =~ /^Database\s+:\s+(.*)/)
                {
                    $database = $1;
                }
                if ($line =~ /^Host\s+:\s+(.*)/)
                {
                    $host = $1;
                }
                if ($line =~ /^Port\s+:\s+(.*)/)
                {
                    $port = $1;
                }
            }
            if ($config_file)
            {
                $options->{config_file} = basename($config_file);
                $options->{status_directory} = dirname($config_file);
                # print "*** $config_file\n";
            }
            else
            {
                die "could not find a config file in $job_file\n";
            }
            my ($pid_file_found, $checked_pid, $running) = get_evaluator_pid($options);
            # print " ($pid_file_found, $checked_pid, $running)\n";
            if ($checked_pid eq $pid)
            {
                if ($pid_file_found && $running)
                {
                    if ((defined($database) && $database eq $what) or ($what eq 'all'))
                    {
                        # stop here
                        kill 15, $pid;
                        unlink $pid_file;
                        unlink $job_file;
                        unlink $xml_file;
                        if ($options->{verbose})
                        {
                            print "# Stopping Evaluator at pid $pid\n ";
                        }
                        if ($options->{remove}{logs} && ($log_file ne '/dev/null'))
                        {
                            unlink $log_file;
                            if ($options->{verbose})
                            {
                                print "# removing log file '$log_file'\n ";
                            }
                        }
                        if ($options->{remove}{database})
                        {
                            my $MYSQL= sprintf( $options->{mysql_bin} .'/mysql -h %s -u%s -p%s -P%d ',
                                $host,
                                $options->{user},
                                $options->{password},
                                $port 
                            );
                            if ($options->{verbose})
                            {
                                print qq($MYSQL -e 'drop schema if exists $database'\n);
                            }
                            system qq($MYSQL -e 'drop schema if exists $database');
                            if ($options->{verbose})
                            {
                                print "# removing database '$database'\n ";
                            }
                        }
                    }
                }
                else
                {
                    # already stopped . Do nothing
                    print "evaluator already stopped (PID $pid)\n";
                    next;
                }
            }
            else
            {
                die "PID mismatch using $pid_file and $job_file\n";
            }
        }
        else 
        {
            die "Job file $job_file not found\n";
        }
    } 
}

sub start_evaluator
{
    my ($options) = @_;
    my $start_time = scalar localtime;
    my $xml_text=<<EVALUATOR_EOF;
    <!DOCTYPE EvaluatorConfiguration SYSTEM "file://someplace/evaluator.dtd">
    <EvaluatorConfiguration 
        name="mysql" 
        testDuration="$options->{test_duration}"
        autoCommit="true" 
        statusInterval="2" 
        htmlFile="mysqlResults.html" >
        <Database driver="com.mysql.jdbc.Driver"        
            url="jdbc:mysql://$options->{host}:$options->{port}/$options->{database_name}?createDatabaseIfNotExist=true"
            user="$options->{user}"
            password="$options->{password}"/> 
         
        <TableGroup 
            name="$options->{table_prefix}" 
            size="$options->{table_size}">
            <ThreadGroup 
                name="A" 
                threadCount="$options->{thread_count}" 
                thinkTime="1"
                updates="$options->{updates}" 
                deletes="$options->{deletes}" 
                inserts="$options->{inserts}" 
                readSize="$options->{read_size}"
                rampUpInterval="$options->{ramp_up_interval}" 
                rampUpIncrement="$options->{ramp_up_increment}"/>
        </TableGroup>
    </EvaluatorConfiguration>

EVALUATOR_EOF
    

    my $create_xml = 1;
    if ($options->{config_file} )
    {
        $create_xml = 0;
    }
    my $evaluator_xml = $options->{config_file} ||  $options->{process_file_prefix} . '.xml';
    my $evaluator_log  = $options->{log_file};
    my $evaluator_pidfile ;
    my $evaluator_job ;
    my $evaluator_path = "$options->{continuent_root}/tungsten/bristlecone/bin/evaluator.sh";
    unless ( -x $evaluator_path)
    {
        die "# Could not find the evaluator launcher at '$evaluator_path'\n";
    }
    my $writing_directory = dirname($evaluator_path);
    $writing_directory =~ s{/bin/?$}{/share};
    if ($options->{status_directory})
    {
        $writing_directory = $options->{status_directory};
    }
    else
    {
        $options->{status_directory} = $writing_directory;
    }
    if ($evaluator_xml =~ /(.*)\.xml$/i)
    {
        $evaluator_log     = "$1.log" unless $evaluator_log;    
        $evaluator_pidfile = "$1.pid";    
        $evaluator_job     = "$1.job";    
    }
    else
    {
        die "the configuration file must be an XML file\n";
    }
    $options->{config_file} = $evaluator_xml;

    my ($pid_file_found, $evaluator_pid, $evaluator_running) = get_evaluator_pid($options);
    # print "pid_file_found: $pid_file_found, pid: $evaluator_pid, running: $evaluator_running\n"; exit;
    my $must_wait_for_cleanup = 0;
    my $MYSQL= sprintf( $options->{mysql_bin} .'/mysql -h %s -u%s -p%s -P%d ',
        $options->{host},
        $options->{user},
        $options->{password},
        $options->{port} 
    );

    if ($evaluator_running && $pid_file_found)
    {
        unless ($evaluator_pid == -1)
        {
            warn "# Evaluator already running with pid $evaluator_pid. Not started.\n";
        }
        return;
    }
    elsif ($pid_file_found)
    {
        # evaluator is not running (because its running time has expired)
        # We remove the  pid file.
        my $pid_file = "$options->{status_directory}/$evaluator_pidfile"; 
        my $log_file = $pid_file;
        my $job_file = $pid_file;
        my $xml_file = $pid_file;
        $xml_file =~ s/\.pid$/.xml/;
        $job_file =~ s/\.pid$/.job/;
        $log_file =~ s/\.pid$/.log/;
        for my $file ( $pid_file, $xml_file, $job_file, $log_file)
        {
            if (-f $file)
            {
                unlink $file;
                print "# removing stale $file\n";
            }
        }
    }
    else
    {
        # evaluator not running. Checking for remnants of database 
        my $result = get_local_result(sprintf(qq[$MYSQL -BN -e 'select count(*) from information_schema.tables where table_schema="%s" and table_name ="%s3"' ],
                $options->{database_name}, $options->{table_prefix}));
        if ($result)
        {
            $must_wait_for_cleanup = 1;
        }
    
    }
    my $child_pid = fork;
    if ($child_pid)
    {
        # 
        # inside the parent process
        #
        if ($must_wait_for_cleanup)
        {
            sleep 5;
        }
        my $timeout = 60;
        my $elapsed = 0;
        my $evaluator_pid;
        while ($elapsed < $timeout)
        {
            ($pid_file_found, $evaluator_pid, $evaluator_running) = get_evaluator_pid($options);
            if (defined $evaluator_pid && $evaluator_pid == -1)
            {
                return;
            }
            last if $evaluator_pid;
            sleep 1;
            $elapsed++;
        }
        unless ($evaluator_pid)
        {
            warn "could not determine evaluator pid \n";
            return;
        }
        $elapsed = 0;
        while ($elapsed < $timeout)
        {
            my $result = get_local_result(sprintf(qq[$MYSQL -BN -e 'select count(*) from information_schema.tables where table_schema="%s" and table_name ="%s3"' ],
                    $options->{database_name}, $options->{table_prefix}));
            if ($result)
            {
                $result = get_local_result(sprintf(qq[$MYSQL -BN -e 'select count(*) from %s.%s3'], 
                        $options->{database_name}, $options->{table_prefix}));
            }
            # print "## ($elapsed) $result\n" if $result;
            if ($result && ($result > 1000))
            {
                #print "ev_pid $evaluator_pid - child_pid: $child_pid\n";
                #print "write_to($writing_directory, $evaluator_pidfile,$evaluator_pid)\n";
                write_to($writing_directory, $evaluator_pidfile,"$evaluator_pid\n");
                my $status_report = 
                     "Task started at   : $start_time \n"
                   . "Task started from : $ENV{PWD} \n"
                   . "Executable        : $evaluator_path\n"
                   . "Process ID        : $evaluator_pid\n"
                   . "Using             : $writing_directory/$evaluator_xml\n"
                   . "Process id        : $writing_directory/$evaluator_pidfile\n";
                if ($options->{log_file})
                {
                    $status_report .= "Log               : $options->{log_file}\n"
                }
                else
                {
                     $status_report .= "Log               : $writing_directory/$evaluator_log\n"
                }
                if ($create_xml)
                {
                     $status_report .= "Database          : $options->{database_name}\n"
                                     . "Table prefix      : $options->{table_prefix}\n"
                                     . "Host              : $options->{host}\n"
                                     . "Port              : $options->{port}\n"
                                     . "User              : $options->{user}\n"
                                     . "Test duration     : $options->{test_duration}\n"
                                     ;
                }
                if ($options->{add_sentinel_tables})
                {
                    $status_report .= "Sentinel tables   : first_table, last_table\n";
                }

                write_to($writing_directory, $evaluator_job, $status_report);
                print "# Evaluator started with pid $evaluator_pid\n"; 
                print "# Evaluator details are available at $writing_directory/$evaluator_job\n"; 
                if ($options->{log_file})
                {
                    print "Evaluator output is being sent to $options->{log_file}\n";
                }
                else
                {
                    print "# Evaluator output can be monitored at $writing_directory/$evaluator_log\n"; 
                }
                print "\n";
                return $evaluator_pid;
            }
            sleep 1;
            $elapsed++;
        }
        # If we reach this point, the evaluator did not start
        return undef;
    }
    else
    {
        # inside the child process
        if ($create_xml)
        {
            write_to($writing_directory, $evaluator_xml, $xml_text);
        }
        # exec "screen -d -m -S evaluator $evaluator_path $evaluator_xml";
        my $log_file = "$writing_directory/$evaluator_log";
        if ($options->{log_file})
        {
            $log_file = $options->{log_file};
        }

        if ($CLI_Utils::VERBOSE)
        {
            print "\n# Running $evaluator_path $writing_directory/$evaluator_xml > $log_file 2>&1 \n";
        }
        my $evaluator_wrapper = $evaluator_xml;
        $evaluator_wrapper =~ s/\.xml$/.sh/;
        write_to($writing_directory, $evaluator_wrapper, 
                "#!/bin/bash\n"
              . "$MYSQL -e 'create schema if not exists $options->{database_name}' \n"
              . "$MYSQL -e 'drop table if exists $options->{database_name}.first_table' \n"
              . "$MYSQL -e 'drop table if exists $options->{database_name}.last_table' \n"
              . "$MYSQL -e 'create table $options->{database_name}.first_table(i int)' \n"
              . "$evaluator_path $writing_directory/$evaluator_xml > $log_file 2>&1\n"
              . "$MYSQL -e 'create schema if not exists $options->{database_name}' \n"
              . "$MYSQL -e 'create table $options->{database_name}.last_table(i int)' \n"
        );
        chmod 0755, "$writing_directory/$evaluator_wrapper";
        if ($options->{add_sentinel_tables})
        {
            exec "$writing_directory/$evaluator_wrapper";
        }
        else
        {
            exec "$evaluator_path $writing_directory/$evaluator_xml > $log_file 2>&1";
        }
    }

    return ;
}

sub get_temp_dir
{
    my $tmpdir = $ENV{TMPDIR} || $ENV{TEMPDIR}  || '/tmp';
    die "can't find temporary directory $tmpdir \n" unless -d $tmpdir;
    return $tmpdir;
}

sub write_to
{
    my ($dir, $fname, $text) = @_;
    unless ($fname)
    {
        print caller, "\n";
        die "no fname set\n";
    }
    my $full_fname = $dir . '/' . $fname;
    open my $FH, '>', $full_fname
        or die "can't create $full_fname ($!)\n";
    print $FH $text;
    close $FH;
}

###########################################################
# Runs a command in the local server and returns the result
###########################################################
sub get_local_result
{
    my ($cmd) = @_;
    if ($CLI_Utils::DEBUG > 2)
    {
        print qq(#i "$cmd"\n);
    }
    my $result = qx($cmd);
    return unless $result;
    if ($CLI_Utils::DEBUG > 2)
    {
        print qq(#o << $result>>\n);
    }
    chomp $result;
    return $result;
}

__END__

=head1 NAME

Concurrent Bristlecone Evaluator wrapper
A tool to launch and manage several instances of Bristlecone database load.

=head1 SYNOPSIS

 $ export CONTINUENT_ROOT=/opt/continuent/cookbook_test
 $ concurrent_evaluator --host=masterhost --port=3306 --user=tungsten_testing --password=private start
  # Evaluator started with pid 18303
  # Evaluator details are available at /opt/continuent/cookbook_test//tungsten/bristlecone/bin/evaluator.job
  # Evaluator output can be monitored at /opt/continuent/cookbook_test//tungsten/bristlecone/bin/evaluator.log    

This command starts one instance of Bristlecone in the current host with the default parameters.

To see the Bristlecone output, you can tail the log file

 $ tail -f /opt/continuent/cookbook_test//tungsten/bristlecone/bin/evaluator.log 

The status of a bristlecone instance can be seen quickly with

 $ concurrent_evaluator status

 $ concurrent_evaluator status
 Evaluator started with pid 18303 (running)

 $ concurrent_evaluator status --verbose
 Evaluator started with pid 18303 (running)
 Task started at   : Fri Jan 11 06:46:39 2013 
 Task started from : /opt/continuent/continuent-tungsten-2.0.0-737 
 Executable        : /opt/continuent/cookbook_test//tungsten/bristlecone/bin/evaluator.sh
 Process ID        : 18303
 Using             : /opt/continuent/cookbook_test//tungsten/bristlecone/bin/evaluator.xml
 Process id        : /opt/continuent/cookbook_test//tungsten/bristlecone/bin/evaluator.pid
 Log               : /opt/continuent/cookbook_test//tungsten/bristlecone/bin/evaluator.log
 Database          : evaluator
 Table prefix      : tbl
 Host              : 127.0.0.1
 Port              : 3306
 User              : tungsten_testing
 Test duration     : 3600

To stop Bristlecone, simply run

 $ concurrent_evaluator stop

You may also want to remove the files and the associated database that were created 

 $ concurrent_evaluator stop --verbose --remove=all
 # Stopping Evaluator at pid 18303
 # removing log file '/opt/continuent/cookbook_test//tungsten/bristlecone/bin/evaluator.log'
 /usr/bin/mysql -h 127.0.0.1 -utungsten_testing -pprivate -P3306  -e 'drop schema if exists evaluator'
 # removing database 'evaluator'

=head1 The Help screen

 $ concurrent_evaluator --help --verbose
 Concurrent Evaluator Wrapper
 version 1.0.1
 (C) Continuent, Inc, 2012-2013
 Syntax: concurrent_evaluator [options] {start|stop|status} [{database|all}] 
    -d --database = name                database where we run the evaluator  (evaluator)
    --host = name                       Host where the database server is running (127.0.0.1)
    -u --user = name                    database user  (tungsten_testing)
    -p --password = name                database password  (private)
    --port = number                     port used to connect to the database (3306)
    -t --table-prefix = name            prefix name to create evaluator tables (tbl)
    -c --config-file = name             File containing evaluator settings 
    -s --status-directory = name        Where the pid and log files will be stored 
    --log-file = name                   File where the evaluator output will be stored 
    -x --process-file-prefix = name     default name for evaluator files  (evaluator)
    -r --continuent-root = name         Where tungsten is installed
                                        (Must have)
    -m --mysql-bin = name               Where mysql binaries are installed (/usr/bin)
    --test-duration = number            How long the test will last (3600)
    --table-size = number               How big is the starting table  (100)
    --inserts = number                  How many inserts in the test  (20)
    --updates = number                  How many updates in the test  (15)
    --deletes = number                  How many deletes in the test  (15)
    --read-size = number                How many rows will be read in the test  (2)
    --ramp-up-interval = number         How long to wait before starting the next group of threads
    --ramp-up-increment = number        How many threads to start in the rampUpIncrement period.
    --thread-count = number             How many threads we will create  (10)
    --instances = number                How many instances of evaluator should we spawn
    --delay = number                    How long it waits after spawing each instance, before installing the next one
    --remove = name                     Used with the "stop" command, it makes the program drop the evaluator installed elements (logs, databases)
                                        (Allowed: {all|database|logs})
    --add-sentinel-tables               Adds "first_table" and "last_table" to each database created.
                                        This is useful to calculate the total time of replication.
    --verbose                           Show more information during installation and help 
    -man --manual                       Display the program manual
    -v --version                        Show ./concurrent_evaluator version and exit 
    --display-options                   Show all the options without running the program
    -h --help                           Display this help

=head1 Operations principles

concurrent_evaluator helps users to launch one or more Bristlecone instances. 
If you want to run a single instance and look at the output on the screen, probably the easiest way is what the manual suggests:
https://docs.continuent.com/wiki/display/TEDOC/Testing+a+cluster+with+Bristlecone

However, if you want to start, monitor, and stop instances inside an application or a script, or if you need to start several instances of Bristlecone, this tool makes it possible, and easy.

This tool works according to some general principles :

=over 3

=item * 
The default configuration file is created dynamically;

=item *
The output of the program is not shown on screen, but redirected to a file.

=item *
For each instance, there will be:

=over 3

=item -
one configuration file (.xml), containing the instructions to start Bristlecone;

=item -
one '.job' file, containing the details of the instance.

=item -
one '.pid' file, containing only the process ID;

=item -
one '.log' file, where the output of the program is redirected.

=back

=item * 
All the files created by concurrent_evaluator are saved inside the Bristlecone directory, under C<./share>. Users can define their own directory.

=item *
concurrent_evaluator can show the status of all the instances that were created.

=item *
concurrent_evaluator can stop instances, either one at the time or all at once.

=back

The reason for the above architectural choices is to make scripting of Bristlecone instances easy, and to create many instances at once without additional work.

=head1 Getting started

To use C<concurrent_evaluator>, you need a working copy of Bristlecone, which comes with either Tungsten Replicator or Continuent Tungsten packages.

As shown in the L<SYNOPSIS> above, you can thus invoke the program with the 'start' command.

=head2 Assumptions

To work properly, you need to indicate where Tungsten was installed. You don't really need Tungsten for this tool to woek, but we assume that Bristlecone was installed inside Tungsten, and that is where we need to find the executables. You can indicate C<CONTINUENT_ROOT> in two ways: Either by using an environmental variable

  export CONTINUENT_ROOT=/some/path/where/Tungsten/was/installed

or by providing the information in the command line

   concurrent_evaluator -r continuent_root=/some/path/where/Tungsten/was/installed [...]

You also need a working C<mysql> client. If none is found in the C<$PATH>, you can supply a suitable path with the option C<--mysql-bin=/some/path>

To connect to a database, there are some default values

    host        -> 127.0.0.1
    port        -> 3306
    user        -> tungsten_testing
    password    -> private

If you are using this command with a replication cluster, you must make sure that the 'host' refers to the master (or use Tungsten connector when available).

=head2 Sample configuration file

This is a sample configuration file, generated by concurrent_evaluator. The lines marked with '***' contain variables that you can define at the command line.

    <!DOCTYPE EvaluatorConfiguration SYSTEM "file://someplace/evaluator.dtd">
    <EvaluatorConfiguration 
        name="mysql" 
        testDuration="3600"    ***
        autoCommit="true" 
        statusInterval="2" 
        htmlFile="mysqlResults.html" >
        <Database driver="com.mysql.jdbc.Driver"        
            url="jdbc:mysql://127.0.0.1:9999/evaluator?createDatabaseIfNotExist=true"  *** ***
            user="tungsten_testing" ***
            password="private"/>   ***
         
        <TableGroup 
            name="tbl"  ***
            size="100"> *** 
            <ThreadGroup 
                name="A" 
                threadCount="10"  ***
                thinkTime="1"
                updates="15"   ***
                deletes="5"    *** 
                inserts="20"   ***
                readSize="2"   ***
                rampUpInterval="0"      ***
                rampUpIncrement="0"/>   ***
        </TableGroup>
    </EvaluatorConfiguration>

If you use the option C<--config-file> to use a different XML file, concurrent_evaluator will not generate an XML file.

=head1 Advanced usage

=head2  Customizing the instance

You can change the following defaults for the dynamically generated configuration file:

    --database = name                   database where we run the evaluator         (evaluator)
    --host = name                       Host where the database server is running   (127.0.0.1)
    --user = name                       database user                               (tungsten_testing)
    --password = name                   database password                           (private)
    --port = number                     port used to connect to the database        (3306)
    --table-prefix = name               prefix name to create evaluator tables      (tbl)
    --test-duration = number            How long the test will last                 (3600)
    --table-size = number               How big is the starting table               (100)
    --thread-count = number             How many threads we will create             (10)

    -c --config-file = name             File containing evaluator settings 
    -s --status-directory = name        Where the pid and log files will be stored 
    --log-file = name                   File where the evaluator output will be stored 
    -x --process-file-prefix = name     default name for evaluator files  (evaluator)


  
You can also change the location where the files are stored. By default, it will be 

    $CONTINUENT_ROOT/tungsten/bristlecone/share

Using C<--status-directory> you can change that location.

The files being created are called 'evaluator.xml', 'evaluator.pid', 'evaluator.log', 'evaluator.job'

The option C<--process-file-prefix> allows you to change the default prefix ('evaluator') into something else.

=head2 Running multiple instances

If you customize each instance, the program will allow you to run more than one instance. 

Rather than customizing manually every instance, though, you can ask for a give number of instances to be generated.
For example:

  $ concurrent_evaluator --instances=10 start 

This command will create 10 instances, using database 'evaluator1', 'evaluator2', 'evaluator3', and so on, and with corresponding 'evaluator1.xml', 'evaluator1.pid', 'evaluator1.log', and so on.

If you know that your evaluator instance requires some time and resources to set up, it is probbaly a good idea NOT to start one instance right after the previous one. You can indicate a delay between instances

  $ concurrent_evaluator --instances=10 --delay=120 start 

With this option, the program will pause 2 minutes (12 seconds) before spawning each instance after the first one.

=head2 Suppressing log files

If you need Bristlecone only for its effects on the database (e.g., producing load) but you don't need the output, you can indicate C</dev/null> as output file. That way, you will save storage.

=head2 Comparing load performance in replicated systems.

One easy way of comparing load in a replicated system is to run the load one server at the time, and measure how long it takes.

This tool makes the task easier, by providing sentinel tables that you can use to measure the running period in different servers.

To compare performance in a system with one master and two slaves, where the slaves are configured differently (for example, one is using parallel replication and one is not), you can do the following:

=over 3

=item 1

Stop the slaves;

=item 2

Start the load in the master, by running

 concurrent_evaluator --port=$MASTER_PORT --instances=10 --add-sentinel-tables

When --add-sentinel-tables is requested, concurrent_evaluator creates a table 'first_table' in each evaluator database, just before the load starts, and a table 'last_table' when the load is over.

=item 3

To determine if the load is completed, and how long did it take, you can run this query:

 select 
    CASE 
        WHEN count_first_table = 0 THEN "LOADING" 
        WHEN (count_first_table = count_last_table) THEN "COMPLETED" 
        ELSE "RUNNING" 
    END as load_status,
    elapsed
 from (
  select 
    (select count(*) from information_schema.tables where table_name='first_table' and table_schema like 'evaluator%') as count_first_table, 
    (select count(*) from information_schema.tables where table_name='last_table' and table_schema like 'evaluator%') as count_last_table, 
    timediff(
        COALESCE((select 
            max(create_time) 
         from information_schema .tables 
         where 
            table_schema like 'evaluator%' 
            and table_name='last_table'
        ), NOW()), 
        (select 
            max(create_time) 
         from information_schema .tables 
         where 
            table_schema like 'evaluator%' 
            and table_name='first_table'
        )
    ) as elapsed 
 ) as t;

=item 4

When the load in the master is completed, start the slaves, one at the time (or all at once if they are on separate hosts), and run the same query to measure the total time.

=back


=head1 COPYRIGHT

Copyright Continuent, Inc, 2013.

=head1 AUTHOR

Written by Giuseppe Maxia, for Continuent, Inc.

