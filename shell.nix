{ }:

let
  # get a normalized set of packages, from which
  # we will install all the needed dependencies
  pkgs = import (fetchTarball {
    name   = "nixos-unstable-2022-05-08";
    url    = "https://github.com/NixOS/nixpkgs/archive/52f31d531b68d92f901310ea3a48999ac182df73.tar.gz";
    sha256 = "1kfvidyc0ipq1f89xgz8i22czss2iry74fwbz5z8rabzzpfzywqr";
  }) {};
in
  pkgs.mkShell {
    buildInputs = [
      pkgs.openjdk
      pkgs.gcc
      pkgs.clang-tools
    ];
    shellHook = ''
      export NIX_ENV=dev
    '';
  }
