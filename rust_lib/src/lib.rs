// use cooklang::{error::PassResult, CooklangParser};

uniffi::include_scaffolding!("cooklang");

pub use std::{ops::RangeInclusive};
pub use cooklang::{parse, error::*, model::*, quantity::*, metadata::*};


impl UniffiCustomTypeConverter for usize {
    type Builtin = usize;

    fn into_custom(val: Self::Builtin) -> uniffi::Result<Self> {
        Ok((val as u64).try_into().unwrap())
    }

    fn from_custom(obj: Self) -> Self::Builtin {
        usize::try_from(obj).unwrap()
    }
}
