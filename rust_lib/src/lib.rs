uniffi::include_scaffolding!("reverse");

pub fn reverse_string(input_string: &str) -> String {
    input_string.chars().rev().collect()
}

pub fn reverse_integer(input_integer: i32) -> i32 {
    let reversed = input_integer.to_string().chars().rev().collect::<String>();
    reversed.parse::<i32>().unwrap()
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn it_reverses_strings() {
        let result = reverse_string("hello world");
        assert_eq!(result, "dlrow olleh");
    }

    #[test]
    fn it_reverses_integers() {
        let result = reverse_integer(123);
        assert_eq!(result, 321);
    }
}